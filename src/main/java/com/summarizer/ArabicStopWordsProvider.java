package com.summarizer;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by praveen on 11/9/14.
 */
public class ArabicStopWordsProvider implements IStopWordProvider
{
        // This array is sorted in the constructor
        private static String[] stopwords = null;

        static
        {
            stopwords = getStopWords();
        }
        public static String[] getStopWords()
        {
            String sdoc = " ";
            try{
                sdoc = Files.toString(new File("stopwords_ar.txt"), Charset.defaultCharset());
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            return sdoc.split("\r\n");
        }

        public boolean isStopWord(String word) {
            if (word == null || "".equals(word)) {
                return false;
            }
            else {
                // search the sorted array for the word, converted to lowercase
                // if it is found, the index will be >= 0
                return (Arrays.binarySearch(stopwords, word.toLowerCase()) >= 0);
            }
        }
}
