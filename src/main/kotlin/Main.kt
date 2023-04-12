import java.io.FileReader
import java.io.File
import java.io.InputStream
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVParserBuilder
import kotlin.math.pow

val FIRST_YEAR = 2004

data class Split_Dataset(val feature_vector: Array<Array<Double>>, val labels: Array<Double>)
data class Split(val feature: Int, val split_index: Int, val squaredError: Double, val rightAverage: Double, val leftAverage: Double)

//need to create node class
open class Node<T> {
    var value: T? = null
    var left: Node<T>? = null
    var right: Node<T>? = null

    constructor(value: T){
        this.value = value
    }
}

open class Comparison_Node<T>(value: T): Node<T>(value) {
    var comparable: T? = null

    constructor(value: T, comparable: T) : this(comparable) {
        this.comparable = comparable
    }
}

class RegressionTree{

    var root: Node<Double>? = null
    var max_depth = 0
    var min_split = 20

    constructor(max_depth: Int){
        this.max_depth = max_depth;
    }

    fun find_best_split(featureVector: Array<Array<Double>>): Split{
        //place split
        //find best split of each feature and compare squared error
        var best_split = find_best_feature_split(featureVector, 0)
        for (i in 1 .. featureVector[0].size){
            val current_split = find_best_feature_split(featureVector, i)
            if(current_split.squaredError < best_split.squaredError){
                best_split = current_split
            }
        }

        return best_split
        //average the label values of left leaf and right leaf
        //find square error of left leaf and right leaf
        //check to see if it is the smallest error
        //if so save it, otherwise go to next split

        //remember minimum split variable
        //can only split a leaf if there are more than min_split datapoints
    }

    //assume already sorted based on feature selected
    //rename the function to have a better title
    fun find_best_feature_split(featureVector: Array<Array<Double>>, featureIndex: Int): Split{
        var i = 1
        var smallestError = -1.0
        var rightAverage = 0.0
        var leftAverage = 0.0
        while (i < featureVector[featureVector.size - 1][featureIndex]){
            val splitError = squared_error(rightAverage, featureVector.copyOfRange(0, i), featureIndex) + squared_error(leftAverage, featureVector.copyOfRange(i, featureVector.size), featureIndex)
            if(smallestError == -1.0 || smallestError > splitError){
                smallestError = splitError
            }
            i++
        }

        return Split(featureIndex, i, smallestError, rightAverage, leftAverage)
    }

    fun build_tree(featureVector: Array<Array<Double>>){
        //find best split
        //create right node
        //create left node
    }

    fun fit(featureVector: Array<Array<Double>>){
        build_tree(featureVector)
    }

    fun predict(){

    }

}

fun main(args: Array<String>) {
    val file = "C:/Users/barra/Desktop/ML Datasets/car mpg dataset/auto-mpg-data.txt"
    var dataset = readFileData(file)
    dataset.forEach { it.forEach { print(it); print(" ") }; println() }
    val (featureVector, labels) = splitDataset(dataset)
    println("Features:")
    featureVector.forEach { it.forEach { print(it); print(" ") }; println() }
//    println("Labels")
//    labels.forEach { println(it) }
    val sortedVector = feature_merge_sort(featureVector, 1, true);
    println("Sorted:")
    sortedVector.forEach { it.forEach { print(it); print(" ") }; println() }
}

//finish this
fun feature_merge_sort(featureVector: Array<Array<Double>>, featureIndex: Int, increasing: Boolean): Array<Array<Double>>{
    return if (featureVector.size <= 1){
        featureVector
    }else if (featureVector.size == 2){
        if (increasing && featureVector[0][featureIndex] > featureVector[1][featureIndex]){
            swap(featureVector, 0, 1)
        } else if (!increasing && featureVector[0][featureIndex] < featureVector[1][featureIndex]){
            swap(featureVector, 0, 1)
        }
        featureVector
    }else{
        feature_merge(
            feature_merge_sort(featureVector.copyOfRange(0, featureVector.size/2), featureIndex, increasing),
            feature_merge_sort(featureVector.copyOfRange(featureVector.size/2, featureVector.size), featureIndex, increasing),
            featureIndex, increasing)
    }
}

fun swap(array: Array<Array<Double>>, index1: Int, index2: Int){
    val temp = array[index1]
    array[index1] = array[index2]
    array[index2] = temp
}

fun feature_merge(featureVector1: Array<Array<Double>>, featureVector2: Array<Array<Double>>, featureIndex: Int, increasing: Boolean): Array<Array<Double>>{
    var mergedArray = emptyArray<Array<Double>>()
    var i = 0
    var j = 0
    if(increasing){
        while (i < featureVector1.size && j < featureVector2.size){
            mergedArray +=
                if(featureVector1[i][featureIndex] < featureVector2[j][featureIndex]){
                    featureVector1[i++]
                }else{
                    featureVector2[j++]
                }
        }
        while (i < featureVector1.size){ mergedArray += featureVector1[i++] }
        while (j < featureVector2.size){ mergedArray += featureVector2[j++] }
    }else{
        while (i < featureVector1.size && j < featureVector2.size){
            mergedArray += if(featureVector1[i][featureIndex] < featureVector2[j][featureIndex]){
                featureVector1[i++]
            }else{
                featureVector2[j++]
            }
        }
        while (i < featureVector1.size){ mergedArray += featureVector1[i++] }
        while (j < featureVector2.size){ mergedArray += featureVector2[j++] }
    }

    return mergedArray
}

/* squared_error function:
 * This calculates the total square error of a set of feature vectors from the average distance given.
 */
fun squared_error(average: Double, featureVector: Array<Array<Double>>, featureIndex: Int) : Double{
    var totalError = 0.0
    for(datapoint in featureVector){
        totalError += (average - datapoint[featureIndex]).pow(2)
    }
    return totalError
}

fun squared_error(dimensions: Int, args: Array<Int>) : Array<Int> {
    val error = Array<Int>(args.size, {i -> 0})
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


