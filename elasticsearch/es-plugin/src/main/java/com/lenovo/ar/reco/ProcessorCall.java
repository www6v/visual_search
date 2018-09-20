package com.lenovo.ar.reco;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProcessorCall {

    public static void indexCall(String fileFullName)  { ///throws IOException
        try {
            Path workDir = Paths.get("/home/wangwei/jniTest/jniReco");  /// ignored
            Process pro = Runtime.getRuntime().exec(String.format("sh /home/wangwei/workspace/visual_search/visual_search/lib/index_image.sh   %s", fileFullName),
                null,
                workDir.toFile());

            int status = 0;
            status = pro.waitFor();
            if (status != 0) {
                //logger.error("Failed to call shell's command." + " shell status: " + status);
                System.out.println("Failed to call shell's command." + " shell status: " + status);
            }

            //logger.info("jniCall  status : " + status);
            System.out.println("jniCall  status : " + status);
        } catch (InterruptedException e) {
            //logger.error("Failed to call shell's command ");
            System.out.println("Failed to call shell's command ");
        } catch(IOException ioe) {
            System.out.println("Failed to call shell's command ");
        }
    }

    public static String execPython(String pythonPath, String[] params) {
        File file = new File(pythonPath);
        if (!file.exists()){
            return "Python script doesnot exist.";
        }

//        String[] command = Arrays.copyOf(new String[]{"search_image.py", pythonPath}, params.length + 2);
//        System.arraycopy(params, 0, command, 2, params.length);
//        String[] command = new String[]{"search_image.py", pythonPath};

        String command = "python /home/wangwei/workspace/visual_search/visual_search/lib/search_image.py";

        List res = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(command, null, null);
            process.waitFor();

            Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                res.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println( "res : " + res.size() );
        if(res.size() !=0 ) {
            for(int i =0; i <= res.size() - 1; i++) {
                System.out.println( "res " + i + ": " + res.get(i) );
            }
        }

        return "success";
    }
}

