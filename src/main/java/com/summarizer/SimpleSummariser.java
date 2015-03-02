package com.summarizer;

/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 Nick Lothian. All rights reserved.
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



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class SimpleSummariser{

    private Integer findMaxValue(List input) {
        Collections.sort(input);
        return (Integer) input.get(0);
    }


    protected Set getMostFrequentWords(int count, Map wordFrequencies,IStopWordProvider stopwords) {
        return Utilities.getMostFrequentWords(count, wordFrequencies,stopwords);
    }

    public String summarise(String input, int numSentences) {
        return summariseInternal(input, numSentences, -1, null, -1,new DefaultStopWordsProvider());
    }

    public String summariseArabic(String input,double percent)
    {
        return summariseInternal(input,0, -1,new DefaultTokenizer(),percent,new DefaultStopWordsProvider());
    }


    protected String summariseInternal(String input, int numSentences, int minWordsInSentence, ITokenizer tokenizer, double percent,IStopWordProvider stopwords) {
        // get the frequency of each word in the input
       // Map wordFrequencies = Utilities.getWordFrequency(input);
        Map wordFrequencies = Utilities.getWordFrequency(input, false, tokenizer, stopwords);
        // now create a set of the X most frequent words
        Set mostFrequentWords = getMostFrequentWords(100, wordFrequencies,stopwords);
        // break the input up into sentences
        // workingSentences is used for the analysis, but
        // actualSentences is used in the results so that the
        // capitalisation will be correct.
        String[] workingSentences = Utilities.getSentences(input.toLowerCase(), minWordsInSentence, tokenizer);
        String[] actualSentences = Utilities.getSentences(input, minWordsInSentence, tokenizer);

        if((percent > 0)&&(percent < 1))
            numSentences = (int)Math.floor(percent*workingSentences.length);
        /*
        System.err.println("Sentences");
        for (int i = 0; i < actualSentences.length; i++) {
            System.err.println(actualSentences[i]);
        }
        */
        // iterate over the most frequent words, and add the first sentence
        // that includes each word to the result
        Set outputSentences = new LinkedHashSet();
        Iterator it = mostFrequentWords.iterator();
        while (it.hasNext()) {
            String word = (String) it.next();
            for (int i = 0; i < workingSentences.length; i++) {
                if (workingSentences[i].indexOf(word) >= 0) {
                    outputSentences.add(actualSentences[i]);
                    break;
                }
                if (outputSentences.size() >= numSentences) {
                    break;
                }
            }
            if (outputSentences.size() >= numSentences) {
                break;
            }

        }
        List reorderedOutputSentences = reorderSentences(outputSentences, input);
        StringBuffer result = new StringBuffer("");
        it = reorderedOutputSentences.iterator();
        while (it.hasNext()) {
            String sentence = (String) it.next();
            result.append(sentence);
            result.append("."); // This isn't always correct - perhaps it should be whatever symbol the sentence finished with
            if (it.hasNext()) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    private List reorderSentences(Set outputSentences, final String input) {
        // reorder the sentences to the order they were in the
        // original text
        ArrayList result = new ArrayList(outputSentences);

        Collections.sort(result, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                String sentence1 = (String) arg0;
                String sentence2 = (String) arg1;

                int indexOfSentence1 = input.indexOf(sentence1.trim());
                int indexOfSentence2 = input.indexOf(sentence2.trim());
                int result = indexOfSentence1 - indexOfSentence2;

                return result;
            }

        });
        return result;
    }



 /*   public String[] getKeywords(String input, int numKeywords) {
        // get the frequency of each word in the input
        Map wordFrequencies = Utilities.getWordFrequency(input);

        //System.out.println(wordFrequencies);

        Set mostFrequentWords = getMostFrequentWords(numKeywords, wordFrequencies);
        //System.out.println(mostFrequentWords);
        String[] results = (String[]) mostFrequentWords.toArray(new String[mostFrequentWords.size()]);
        if (results.length <= numKeywords) {
            return results;
        } else {
            String[] newResults = new String[numKeywords];
            System.arraycopy(results, 0, newResults, 0, numKeywords);
            return newResults;
        }
    }*/

}
