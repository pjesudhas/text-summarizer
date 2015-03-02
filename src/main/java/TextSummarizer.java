/**
 * Created by praveen on 5/9/14.
 */
import com.summarizer.*;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


public class TextSummarizer
{
    public static void main(String args[])
    {
        if(args.length == 2)
        {
            try {
                SimpleSummariser obj = new SimpleSummariser();
                String sdoc = Files.toString(new File(args[0]), Charset.defaultCharset());
                sdoc = sdoc.trim();
                String[] lines = sdoc.split(".");
                String trial = obj.summariseArabic(sdoc,Double.parseDouble(args[1]));//REducing no of lines by 50%
                trial = trial.replace("..",".");
                System.out.println(trial);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Sample execution : java -jar jarname.jar textfile %of compression");
        }
    }
}
