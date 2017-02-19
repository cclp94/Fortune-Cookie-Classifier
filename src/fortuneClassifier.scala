import scala.collection.mutable.HashMap;
import scala.collection.mutable.ListBuffer;
import scala.io._;
import java.io._;

object FortuneClassifier{
    def main(args : Array[String]){
        val STOP_LIST = args(0);
        val TRAINING_DATA = args(1);
        val TRAINING_LABELS = args(2);
        val TEST_DATA = args(3);
        val TEST_LABELS = args(4);
        // Collect stopList Data
        val stopList = Source.fromFile(STOP_LIST).getLines.toList;
        //=============================PREPROCESSING================================
        println("PREPROCESSING TRAINING DATA");
        // Get Training Data
        var training = Source.fromFile(TRAINING_DATA).getLines.toList;
        // Get Training Labels
        var trainLabels = Source.fromFile(TRAINING_LABELS).getLines.toArray;

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
        println("PREPROCESSING COMPLETE");
        val filteredData = trainingData.toList;
        // Filter Vocabulary
        val vocabulary = vocabularyBuffer.toList.distinct.sortWith(_ < _); 
        // Create Map of vocabulary with array index for easy search       
        val vocabMap = collection.mutable.Map(vocabulary.zipWithIndex.map{ 
                            case (e, i) => (e, Array(i, 1, 1));
                        }.toMap.toSeq: _*);

        var preprocessed = ListBuffer[Array[Int]]();
        var numberOfFortunesInClass = new Array[Int](2);
        var totalNumberOfWords = 0;
        println("STARTING TRAINING PHASE");
        // For each fortune and label assigned to it
        for((fortuneCookie, trainedLabel) <- (filteredData zip trainLabels)){
            // Create array of size vocabulary.length + 1
            var preprocessedCookie = Array.fill(vocabulary.length+1)(0);
            for(feature <- fortuneCookie.trim().split(" ")){
                if(!feature.isEmpty()){
                    // assign 1 for features present in the entry
                    preprocessedCookie(vocabMap(feature)(0)) = 1;
                    // Increment number of times word was wise fortune or prediction
                    if(trainedLabel.toInt == 1)
                        vocabMap(feature)(2) += 1;
                    else
                        vocabMap(feature)(1) += 1;

                    totalNumberOfWords += 1;
                }
            }
            // Assign label to data
            preprocessedCookie(vocabulary.length) = trainedLabel.toInt;
            // Increment total number of wise fortunes or predictions
            numberOfFortunesInClass(trainedLabel.toInt) += 1;
            // Add preprocessed trained data
            preprocessed += preprocessedCookie;
        }
        println("TRAINING DONE, RESULTS IN ./data/preprocessed.txt");
        // Write preprocessed data in file
        val writer = new PrintWriter(new File("./data/preprocessed.txt"));
        writer.write(vocabulary.toList.mkString(", ")+"\n");
        for(cookie <- preprocessed){
            writer.write(cookie.mkString(", ")+"\n");
        }
        writer.close();
        //===========================END TRAINING=====================================
        //---------------------------TESTTING PHASE-----------------------------------
        println("STARTING TEST PHASE");
        var testRaw = Source.fromFile(TEST_DATA).getLines.toArray;
        // For every line in the test data
        var testDataBuffer = ListBuffer[String]();
        for(t <- testRaw) {
            // Filter every feature using a REGEX made from the stop list
            val splitted = t.split(" ").filterNot(_.matches("^("+stopList.mkString("|")+")$"));
            // Add filtered fortune cookie to testDataBuffer
            testDataBuffer += splitted.mkString(" ");
        }
        // Covert to imutable list for faster access
        var testData = testDataBuffer.toList;

        // Analyse data
        var preprocessedTestData = ListBuffer[Array[Int]]();
        for(fortune <- testData) {
            var preprocessedCookie = Array.fill(vocabulary.length)(0);
            for(feature <- fortune.trim().split(" ")){
                if(!feature.isEmpty() && vocabMap.contains(feature)){
                    // assign 1 for features present in the entry
                    preprocessedCookie(vocabMap(feature)(0)) = 1;
                }
            }
            preprocessedTestData += preprocessedCookie;
        }
        var maxScore = -1.0f;
        var totalFortunes = 0;
        numberOfFortunesInClass.foreach(totalFortunes += _);
        var winner = 0;
        var results = ListBuffer[Int]();
        for(fortune <- preprocessedTestData){
            for(i <- 0 to 1) {
                val pB = numberOfFortunesInClass(i).toFloat/totalFortunes;
                var score = (numberOfFortunesInClass(i).toFloat / totalFortunes);
                for(j <- 0 to fortune.length-1){
                    if(fortune(j) == 1){
                        val feature = vocabMap(vocabulary(j));
                        val pBPipeA = feature(i+1).toFloat/(feature(1)+feature(2));
                        val pA = (feature(1)+feature(2)).toFloat/totalNumberOfWords;
                        score = score * ((pBPipeA * pA).toFloat/pB);

                    }
                }
                if(score >= maxScore){
                maxScore = score;
                    winner = i;
                }
            }
            results += winner;
            maxScore = -1.0f;
        }
        println("TESTING DONE");
        var testLabels = Source.fromFile(TEST_LABELS).getLines.toArray;
        var totalCorrect = 0;
        for((r1, r2) <- results zip testLabels){
            
            if(r1.toInt == r2.toInt){
                totalCorrect += 1;
            }
        }
        println((totalCorrect.toFloat/results.toList.length * 100 )+ "% Accuracy");
    }
}