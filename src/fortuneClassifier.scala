import scala.collection.mutable.HashMap;
import scala.collection.mutable.ListBuffer;
import scala.io._;
import java.io._;

object FortuneClassifier{
    def main(args : Array[String]){
        // Collect stopList Data
        val stopList = Source.fromFile("./data/stoplist.txt" ).getLines.toList;


        //=============================TRAINING================================
        // Get Training Data
        var training = Source.fromFile("./data/training/traindata.txt").getLines.toList;
        // Get Training Labels
        var trainLabels = Source.fromFile("./data/training/trainlabels.txt").getLines.toArray;

        var vocabularyBuffer = ListBuffer[String]();
        var trainingData = ListBuffer[String]();
        // For every line in the training data
        for(t <- training) {
            // Filter every feature using a REGEX made from the stop list
            val splitted = t.split(" ").filterNot(_.matches("^("+stopList.mkString("|")+")$"));
            // Added filtered list to vocabulary
            splitted.foreach{vocabularyBuffer += _;}
            // Add filtered fortune cookie to trainingData
            trainingData += splitted.mkString(" ");
        }
        val filteredData = trainingData.toList;
        // Filter Vocabulary
        val vocabulary = vocabularyBuffer.toList.distinct.sortWith(_ < _); 
        // Create Map of vocabulary with array index for easy search       
        val vocabMap = vocabulary.zipWithIndex.map{ 
                            case (e, i) => (e, i);
                        }.toMap;

        var preprocessed = ListBuffer[Array[Int]]();
        // For each fortune and label assigned to it
        for((fortuneCookie, trainedLabel) <- (filteredData zip trainLabels)){
            // Create array of size vocabulary.length + 1
            var preprocessedCookie = Array.fill(vocabulary.length+1)(0);
            for(feature <- fortuneCookie.trim().split(" ")){
                if(!feature.isEmpty()){
                    // assign 1 for features present in the entry
                    preprocessedCookie(vocabMap(feature)) = 1;
                }
            }
            // Assign label to data
            preprocessedCookie(vocabulary.length) = trainedLabel.toInt;
            // Add preprocessed trained data
            preprocessed += preprocessedCookie;
        }
        // Write preprocessed data in file
        val writer = new PrintWriter(new File("./data/preprocessed.txt"));
        writer.write(vocabulary.toList.mkString(", ")+"\n");
        for(cookie <- preprocessed){
            writer.write(cookie.mkString(", ")+"\n");
        }
        writer.close();
        //===========================END TRAINING=====================================
    }
}