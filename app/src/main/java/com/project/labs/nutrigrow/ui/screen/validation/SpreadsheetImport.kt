package com.project.labs.nutrigrow.ui.screen.validation

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

val SPREADSHEET_MIME_TYPES = arrayOf(
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/vnd.ms-excel",
    "text/csv",
    "text/comma-separated-values",
    "text/plain",
    "application/octet-stream",
)

class SpreadsheetException(message: String) : Exception(message)

fun importSampleSet(context: Context, uri: Uri): ValidationSampleSet {
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        ?: throw SpreadsheetException("Berkas tidak dapat dibuka.")

    val name = displayName(context, uri)

    val table = if (isZip(bytes)) readWorkbook(bytes) else readDelimited(bytes)
    val samples = toSamples(table)

    if (samples.isEmpty()) {
        throw SpreadsheetException(
            "Tidak ada baris data yang terbaca. Pastikan berkas memuat kolom jenis kelamin, " +
                "umur, berat badan, tinggi badan, indeks, dan z-score referensi."
        )
    }

    return ValidationSampleSet(
        title = "Sampel dari berkas",
        source = name,
        samples = samples,
    )
}

private fun isZip(bytes: ByteArray): Boolean =
    bytes.size > 1 && bytes[0] == 'P'.code.toByte() && bytes[1] == 'K'.code.toByte()

private fun displayName(context: Context, uri: Uri): String {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) {
            return cursor.getString(index) ?: "berkas"
        }
    }
    return uri.lastPathSegment ?: "berkas"
}

private fun readWorkbook(bytes: ByteArray): List<List<String>> {
    val entries = mutableMapOf<String, ByteArray>()

    ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                entries[entry.name] = zip.readBytes()
            }
            zip.closeEntry()
            entry = zip.nextEntry
        }
    }

    val shared = entries["xl/sharedStrings.xml"]?.let { parseSharedStrings(it) } ?: emptyList()

    val sheets = entries.keys
        .filter { it.startsWith("xl/worksheets/") && it.endsWith(".xml") }
        .sorted()

    if (sheets.isEmpty()) {
        throw SpreadsheetException("Berkas Excel tidak memuat lembar kerja.")
    }

    for (sheet in sheets) {
        val table = parseSheet(entries.getValue(sheet), shared)
        if (findHeaderRow(table) != null) return table
    }

    throw SpreadsheetException(
        "Baris judul kolom tidak ditemukan pada lembar mana pun. Judul kolom yang dicari " +
            "memuat kata seperti \"Jenis Kelamin\", \"Umur\", \"BB\", \"TB\", dan \"Indeks\"."
    )
}

private fun parseSharedStrings(bytes: ByteArray): List<String> {
    val strings = mutableListOf<String>()
    val parser = Xml.newPullParser()
    parser.setInput(ByteArrayInputStream(bytes), null)

    var current = StringBuilder()
    var insideItem = false
    var event = parser.eventType

    while (event != XmlPullParser.END_DOCUMENT) {
        when (event) {
            XmlPullParser.START_TAG -> when (parser.name) {
                "si" -> {
                    insideItem = true
                    current = StringBuilder()
                }
                "t" -> if (insideItem) current.append(parser.nextText())
            }
            XmlPullParser.END_TAG -> if (parser.name == "si" && insideItem) {
                strings.add(current.toString())
                insideItem = false
            }
        }
        event = parser.next()
    }

    return strings
}

private fun parseSheet(bytes: ByteArray, shared: List<String>): List<List<String>> {
    val rows = mutableListOf<List<String>>()
    val parser = Xml.newPullParser()
    parser.setInput(ByteArrayInputStream(bytes), null)

    var cells = mutableMapOf<Int, String>()
    var column = -1
    var type: String? = null
    var raw: String? = null
    var event = parser.eventType

    while (event != XmlPullParser.END_DOCUMENT) {
        when (event) {
            XmlPullParser.START_TAG -> when (parser.name) {
                "row" -> cells = mutableMapOf()
                "c" -> {
                    column = columnIndex(parser.getAttributeValue(null, "r") ?: "")
                    type = parser.getAttributeValue(null, "t")
                    raw = null
                }
                "v" -> raw = parser.nextText()
                "t" -> if (raw == null) raw = parser.nextText()
            }
            XmlPullParser.END_TAG -> when (parser.name) {
                "c" -> {
                    val value = resolveCell(raw, type, shared)
                    if (column >= 0 && value.isNotBlank()) cells[column] = value
                }
                "row" -> rows.add(flatten(cells))
            }
        }
        event = parser.next()
    }

    return rows
}

private fun resolveCell(raw: String?, type: String?, shared: List<String>): String {
    val value = raw?.trim() ?: return ""
    if (type != "s") return value
    val index = value.toIntOrNull() ?: return value
    return shared.getOrNull(index) ?: ""
}

private fun flatten(cells: Map<Int, String>): List<String> {
    val last = cells.keys.maxOrNull() ?: return emptyList()
    return (0..last).map { cells[it] ?: "" }
}

private fun columnIndex(reference: String): Int {
    var index = 0
    for (character in reference) {
        if (!character.isLetter()) break
        index = index * 26 + (character.uppercaseChar() - 'A' + 1)
    }
    return index - 1
}

private fun readDelimited(bytes: ByteArray): List<List<String>> {
    val text = String(bytes, Charsets.UTF_8)
    val lines = text.lines().filter { it.isNotBlank() }
    if (lines.isEmpty()) throw SpreadsheetException("Berkas kosong.")

    val header = lines.first()
    val separator = listOf(';', '\t', ',').maxByOrNull { candidate ->
        header.count { it == candidate }
    } ?: ','

    return lines.map { line ->
        line.split(separator).map { it.trim().trim('"') }
    }
}

private class ColumnMap(
    val gender: Int,
    val age: Int,
    val weight: Int,
    val height: Int,
    val index: Int,
    val referenceZ: Int,
    val referenceLabel: Int,
    val number: Int,
)

private fun normalise(value: String): String =
    value.lowercase().replace(Regex("\\s+"), " ").trim()

private fun findColumn(header: List<String>, matches: (String) -> Boolean): Int =
    header.indexOfFirst { matches(normalise(it)) }

private fun mapColumns(header: List<String>): ColumnMap? {
    val gender = findColumn(header) { it.contains("kelamin") || it == "jk" || it.contains("gender") }
    val age = findColumn(header) { it.contains("umur") || it.contains("usia") }
    val weight = findColumn(header) { it.startsWith("bb") || it.contains("berat") }
    val height = findColumn(header) { it.startsWith("tb") || it.contains("tinggi") || it.contains("panjang") }
    val index = findColumn(header) { it.contains("indeks") || it.contains("indikator") }

    val referenceZ = findColumn(header) {
        it.contains("z") && isReference(it) && !it.contains("aplikasi")
    }
    val referenceLabel = findColumn(header) {
        it.contains("klasifikasi") && isReference(it) && !it.contains("aplikasi")
    }
    val number = findColumn(header) { it == "no" || it.startsWith("no ") || it.contains("nomor") }

    if (gender < 0 || age < 0 || weight < 0 || height < 0 || index < 0 || referenceZ < 0) return null

    return ColumnMap(gender, age, weight, height, index, referenceZ, referenceLabel, number)
}

private fun isReference(header: String): Boolean =
    header.contains("referensi") || header.contains("rujukan") || header.contains("manual")

private fun findHeaderRow(rows: List<List<String>>): Pair<Int, ColumnMap>? {
    rows.forEachIndexed { position, row ->
        val columns = mapColumns(row)
        if (columns != null) return position to columns
    }
    return null
}

private fun toSamples(rows: List<List<String>>): List<ValidationSample> {
    val (headerRow, columns) = findHeaderRow(rows)
        ?: throw SpreadsheetException(
            "Baris judul kolom tidak ditemukan. Kolom yang wajib ada: jenis kelamin, umur, " +
                "berat badan, tinggi badan, indeks, dan z-score referensi."
        )

    val samples = mutableListOf<ValidationSample>()

    rows.drop(headerRow + 1).forEachIndexed { position, row ->
        val gender = readGender(row.getOrNull(columns.gender)) ?: return@forEachIndexed
        val age = readNumber(row.getOrNull(columns.age))?.toInt() ?: return@forEachIndexed
        val weight = readNumber(row.getOrNull(columns.weight)) ?: return@forEachIndexed
        val height = readNumber(row.getOrNull(columns.height)) ?: return@forEachIndexed
        val index = readIndex(row.getOrNull(columns.index)) ?: return@forEachIndexed
        val referenceZ = readNumber(row.getOrNull(columns.referenceZ)) ?: return@forEachIndexed

        val number = columns.number
            .takeIf { it >= 0 }
            ?.let { readNumber(row.getOrNull(it))?.toInt() }
            ?: (position + 1)

        val label = columns.referenceLabel
            .takeIf { it >= 0 }
            ?.let { row.getOrNull(it)?.trim() }
            ?.takeIf { it.isNotBlank() }
            ?: "-"

        samples.add(
            ValidationSample(
                no = number,
                gender = gender,
                age = age,
                weight = weight,
                height = height,
                index = index,
                ref_z = referenceZ,
                ref_label = label,
            )
        )
    }

    return samples
}

private fun readGender(value: String?): String? {
    val text = normalise(value ?: return null)
    return when {
        text.startsWith("l") || text == "m" || text.startsWith("laki") || text.startsWith("male") -> "M"
        text.startsWith("p") || text == "f" || text.startsWith("perempuan") || text.startsWith("female") -> "F"
        else -> null
    }
}

private fun readNumber(value: String?): Double? =
    value?.trim()?.replace(',', '.')?.toDoubleOrNull()

private fun readIndex(value: String?): String? {
    val text = normalise(value ?: return null).replace(" ", "").replace("pb", "tb")
    return when {
        text.contains("bb/tb") -> "BB/TB"
        text.contains("bb/u") -> "BB/U"
        text.contains("tb/u") -> "TB/U"
        else -> null
    }
}
