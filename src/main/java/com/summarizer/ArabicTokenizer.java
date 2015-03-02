package com.summarizer;

//import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
//import edu.stanford.nlp.ling.HasWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by praveen on 10/9/14.
 */
public class ArabicTokenizer implements ITokenizer
{
//    static ArabicSegmenter segObj = null;
    static Properties props = null;
    static
    {
//        segObj = new ArabicSegmenter(getProperties());
 //       segObj.loadSegmenter("arabic-segmenter-atb+bn+arztrain.ser.gz",getProperties());
    }
    static Properties getProperties() {
        if (props == null) {
            props = new Properties();
            props.setProperty("inputEncoding", "UTF-8");
            props.setProperty("MAX_ITEMS", "500000");
        }
        return props;
    }

    public String[] tokenize(String input)
    {
   //     List<HasWord> temp = segObj.segment (input);
        List<String> tokens = new ArrayList<String>();
     //   for(int i=0; i < temp.size();i++)
      //  {
       //     tokens.add(i,temp.get(i).word());
      //  }
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }
}
