import java.io.FileReader
import java.io.File
import java.io.InputStream
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVParserBuilder

val FIRST_YEAR = 2004

data class Split_Dataset(val feature_vector: Array<Array<Double>>, val labels: Array<Double>)

class RegressionTree{

    var root = 0
    var max_depth = 0

    constructor(max_depth: Int){
        this.max_depth = max_depth;
    }

    fun find_best_split(){

    }

    fun build_tree(){

    }

    fun fit(){

    }

    fun predict(){

    }

}

fun main(args: Array<String>) {
    val file = "C:/Users/barra/Desktop/ML Datasets/car mpg dataset/auto-mpg-data.txt"
    var dataset = readFileData(file)
    dataset.forEach { it.forEach { print(it); print(" ") }; println() }
    val (feature_vector, labels) = splitDataset(dataset)
    println("Features:")
    feature_vector.forEach { it.forEach { print(it); print(" ") }; println() }
    println("Labels")
    labels.forEach { println(it) }

}

fun squared_error(dimensions: Int, args: Array<Int>) : Array<Int> {
    val error = Array<Int>(dimensions, {i -> 0})
    for (i in 0 until dimensions){
        error[i] = (args[i] - args[i - dimensions]) * (args[i] - args[i - dimensions])
    }

    return error
}

fun readFileData(file: String) : Array<Array<Double>> {

    val inputStream: InputStream = File(file).inputStream()
    //val lineList = mutableListOf<String>()
    var dataset = emptyList<Array<Double>>()
    var i = 0
    inputStream.bufferedReader().forEachLine {
        val tempList = it.split(" ").filterNot{ it.isEmpty()}.subList(0,8)
        val tempList2 = (tempList.subList(0,7) + (tempList[7][0]).toString())
        var tempArray = emptyArray<Double>()
        tempList2.forEach { tempArray += it.toDouble() }
        val tempList3 = listOf<Array<Double>>(tempArray)
        dataset += tempList3
    }

    return dataset.toTypedArray()
}

fun splitDataset(dataset: Array<Array<Double>>) : Split_Dataset {
    var labels = emptyArray<Double>()
    var features = emptyArray<Array<Double>>()
    dataset.forEach { labels += it[0] }
    dataset.forEach { features += it.copyOfRange(1, dataset[0].size) }
    return Split_Dataset(features, labels)
}

fun readCSVToArray(file: String){
    try {

        val csvReader = CSVReaderBuilder(FileReader(file))
            .withCSVParser(CSVParserBuilder().withSeparator(';').build())
            .build()

        // Maybe do something with the header if there is one
        val header = csvReader.readNext()

        // Read the rest
        var line: Array<String>? = csvReader.readNext()
        while (line != null) {
            // Do something with the data
            println(line[0] + " " + line[1] + " " + line[2])

            line = csvReader.readNext()
        }
    } catch (e : Exception) {
        e.printStackTrace()
    }
}

fun convertDateToTime(date: String) : Int{
    val month = date.substring(0,2).toInt()
    val day = date.substring(3,5).toInt()
    val year = date.substring(6,10).toInt() - FIRST_YEAR

    var time = 0

    time += day

    when (month) {
        1  -> time += 0
        2  -> time += 31
        3  -> time += 31+28
        4  -> time += 31+28+31
        5  -> time += 31+28+31+30
        6  -> time += 31+28+31+30+31
        7  -> time += 31+28+31+30+31+30
        8  -> time += 31+28+31+30+31+30+31
        9  -> time += 31+28+31+30+31+30+31+31
        10 -> time += 31+28+31+30+31+30+31+31+30
        11 -> time += 31+28+31+30+31+30+31+31+30+31
        12 -> time += 31+28+31+30+31+30+31+31+30+31+30
    }
    if (month > 2 && year % 4 == 0) time += 1

    time += year * 365 + (year/4).toInt()

    return time
}


