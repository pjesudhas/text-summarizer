package com.summarizer;
/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Nick Lothian. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        developers of Classifier4J (http://classifier4j.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "Classifier4J" must not be used to endorse or promote
 *    products derived from this software without prior written
 *    permission. For written permission, please contact
 *    http://sourceforge.net/users/nicklothian/.
 *
 * 5. Products derived from this software may not be called
 *    "Classifier4J", nor may "Classifier4J" appear in their names
 *    without prior written permission. For written permission, please
 *    contact http://sourceforge.net/users/nicklothian/.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */


import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.io.*;

import java.util.*;

/**
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class Utilities {

    public static Map getWordFrequency(String input) {
        return getWordFrequency(input, false);
    }

    public static Map getWordFrequency(String input, boolean caseSensitive) {
        return getWordFrequency(input, caseSensitive, new DefaultTokenizer(), new DefaultStopWordsProvider());
    }

    /**
     * Get a Map of words and Integer representing the number of each word
     *
     * @param input The String to get the word frequency of
     * @param caseSensitive true if words should be treated as separate if they have different case
     * @param tokenizer a junit.framework.TestCase#run()
     * @param stopWordsProvider
     * @return
     */
    public static Map getWordFrequency(String input, boolean caseSensitive, ITokenizer tokenizer, IStopWordProvider stopWordsProvider) {
        String convertedInput = input;
        if (!caseSensitive) {
            convertedInput = input.toLowerCase();
        }

        // tokenize into an array of words
        String[] words = tokenizer.tokenize(convertedInput);
        Arrays.sort(words);

        String[] uniqueWords = getUniqueWords(words);

        Map result = new HashMap();
        for (int i = 0; i < uniqueWords.length; i++) {
            if (stopWordsProvider == null) {
                // no stop word provider, so add all words
                result.put(uniqueWords[i], new Integer(countWords(uniqueWords[i], words)));
            } else if (isWord(uniqueWords[i]) && !stopWordsProvider.isStopWord(uniqueWords[i])) {
                // add only words that are not stop words
                result.put(uniqueWords[i], new Integer(countWords(uniqueWords[i], words)));
            }
        }

        return result;
    }

    private static String[] findWordsWithFrequency(Map wordFrequencies, Integer frequency, IStopWordProvider stopWordProvider) {
        if (wordFrequencies == null || frequency == null) {
            return new String[0];
        } else {
            List results = new ArrayList();
            Iterator it = wordFrequencies.keySet().iterator();

            while (it.hasNext()) {
                String word = (String) it.next();
                if (frequency.equals(wordFrequencies.get(word))) {
                    if (stopWordProvider != null) {
                        // we have a stop word provider
                        if (!stopWordProvider.isStopWord(word)) {
                            // this is not a stop word
                            results.add(word);
                        }
                    } else {
                        results.add(word);
                    }
                }
            }

            return (String[]) results.toArray(new String[results.size()]);

        }
    }

    public static Set getMostFrequentWords(int count, Map wordFrequencies,IStopWordProvider stopWords) {
        return getMostFrequentWords(count, wordFrequencies, true, stopWords);
    }

    public static Set getMostFrequentWords(int count, Map wordFrequencies, boolean includeSingleOccurances, IStopWordProvider stopWordProvider) {
        Set result = new LinkedHashSet();

        Integer max = new Integer(0);
        if (wordFrequencies.size() > 0) {
            max = (Integer) Collections.max(wordFrequencies.values());
        }

        int stop = 1;
        if (includeSingleOccurances) {
            stop = 0;
        }

        int freq = max.intValue();
        while (result.size() < count && freq > stop) {
            // this is very icky
            String words[] = findWordsWithFrequency(wordFrequencies, new Integer(freq), stopWordProvider);
            result.addAll(Arrays.asList(words));
            freq--;
        }

        return result;
    }


    private static boolean isWord(String word) {
        if (word != null && !word.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find all unique words in an array of words
     *
     * @param input an array of Strings
     * @return an array of all unique strings. Order is not guarenteed
     */
    public static String[] getUniqueWords(String[] input) {
        if (input == null) {
            return new String[0];
        } else {
            Set result = new TreeSet();
            for (int i = 0; i < input.length; i++) {
                result.add(input[i]);
            }
            return (String[]) result.toArray(new String[result.size()]);
        }
    }

    /**
     * Count how many times a word appears in an array of words
     *
     * @param word The word to count
     * @param words non-null array of words
     */
    public static int countWords(String word, String[] words) {
        // find the index of one of the items in the array.
        // From the JDK docs on binarySearch:
        // If the array contains multiple elements equal to the specified object, there is no guarantee which one will be found.
        int itemIndex = Arrays.binarySearch(words, word);

        // iterate backwards until we find the first match
        if (itemIndex > 0) {
            while (itemIndex > 0 && words[itemIndex].equals(word)) {
                itemIndex--;
            }
        }

        // now itemIndex is one item before the start of the words
        int count = 0;
        while (itemIndex < words.length && itemIndex >= 0) {
            if (words[itemIndex].equals(word)) {
                count++;
            }

            itemIndex++;
            if (itemIndex < words.length) {
                if (!words[itemIndex].equals(word)) {
                    break;
                }
            }
        }

        return count;
    }

    public static String[] getSentences(String input) {
        return getSentences(input, -1, null);
    }

    /**
     *
     * @param input a String which may contain many sentences
     * @param minSentenceLength the minumim lenght of a sentence. Pass -1 if this shoudl not be checked
     * @return an array of Strings, each element containing a sentence
     */
    public static String[] getSentences(String input, int minSentenceLength, ITokenizer tokenizer) {
        if (input == null) {
            return new String[0];
        } else {
            // split on a ".", a "!", a "?" followed by a space or EOL
            String[] sentences = sentenceTokonizer(input);
            if (minSentenceLength <= 0) {
                return sentences;
            } else {
                if (tokenizer == null) {
                    tokenizer = new DefaultTokenizer();
                }
                List results = new ArrayList();
                for (int i = 0; i < sentences.length; i++) {
                    String[] words = tokenizer.tokenize(sentences[i]);
                    if (words.length > minSentenceLength) {
                        results.add(sentences[i]);
                    }
                }
                return (String[]) results.toArray(new String[results.size()]);
            }
        }
    }

    public static String[] sentenceTokonizer(String entireDoc)
    {
        Reader reader = new StringReader(entireDoc);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        List<String> sentenceList = new LinkedList<String>();
        Iterator<List<HasWord>> it = dp.iterator();

        while (it.hasNext())
        {
            StringBuilder sentenceSb = new StringBuilder();
            List<HasWord> sentence = it.next();
            for (HasWord token : sentence)
            {
                if(sentenceSb.length()>1)
                {
                    sentenceSb.append(" ");
                }
                sentenceSb.append(token.word());
            }
            sentenceList.add(sentenceSb.toString());
        }
        return (String[]) sentenceList.toArray(new String[sentenceList.size()]);
    }

    /**
     * Given an inputStream, this method returns a String. New lines are
     * replaced with " "
     */
    public static String getString(InputStream is) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = "";
        StringBuffer stringBuffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append(" ");
        }

        reader.close();

        return stringBuffer.toString().trim();
    }
}

