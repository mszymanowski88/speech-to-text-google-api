package com.example.demo;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

    private final String UPLOAD_DIR = "C:/ProgramyJava/demo9/src/main/resources/upload/";
    String fileText;

    @Autowired
    ServletContext context;

    @GetMapping("/")
    public String homepage( Model model) {

        model.addAttribute("transcription", fileText );
        return "index";
    }


    public static void deleteFolder() throws IOException {

        Files.walk(Paths.get("src/main/resources/upload/"))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Model model) throws IOException {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }


        String filePath = context.getRealPath("/");
        File f1 = new File(filePath+"/"+file.getOriginalFilename());
        System.out.println("moze"+filePath);
        file.transferTo(Paths.get("C:/ProgramyJava/demo9/src/main/resources/upload/"+file.getOriginalFilename()));
        System.out.println("C:/ProgramyJava/demo9/src/main/resources/upload/"+file.getOriginalFilename());



        try (SpeechClient speech = SpeechClient.create()) {
            Path path = Paths.get(UPLOAD_DIR+file.getOriginalFilename());

            byte[] content = Files.readAllBytes(path);
            System.out.println("path" + path);

            // Configure request with video media type
            RecognitionConfig recConfig =
                    RecognitionConfig.newBuilder()
                            // encoding may either be omitted or must match the value in the file header
                            .setEncoding(RecognitionConfig.AudioEncoding.OGG_OPUS)
                            .setLanguageCode("en-US")
                            // sample rate hertz may be either be omitted or must match the value in the file
                            // header
                            .setSampleRateHertz(16000)
                            .setModel("video")
                            .build();

            RecognitionAudio recognitionAudio =
                    RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(content)).build();

            RecognizeResponse recognizeResponse = speech.recognize(recConfig, recognitionAudio);
            // Just print the first result here.
            SpeechRecognitionResult result = recognizeResponse.getResultsList().get(0);
            // There can be several alternative transcripts for a given chunk of speech. Just use the
            // first (most likely) one here.
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            System.out.printf("Transcript : %s\n", alternative.getTranscript());

            model.addAttribute("transcription", "alternative.getTranscript()" );
            fileText = alternative.getTranscript();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            deleteFolder();
        }




        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded " +file.getOriginalFilename() + '!');

        return "redirect:/";
    }


//    @PostMapping("/upload")
//    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Model model) throws IOException {
//
//        // check if file is empty
//        if (file.isEmpty()) {
//            attributes.addFlashAttribute("message", "Please select a file to upload.");
//            return "redirect:/";
//        }
//
//
//
//
////        File filePath = new File(String.valueOf(file));
////        System.out.println("Moze to" + filePath.getAbsolutePath());
//
//        // normalize the file path
//
//
//        System.out.println("15"+ file.getOriginalFilename());
//
//
//
//        Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
//
//        byte[] content = Files.readAllBytes(path);
//        System.out.println("path" + path);
//
////        System.out.println("do tond");
////
////        String path1 = file1.getAbsolutePath();
////
////        System.out.println(fileName);
////        System.out.println("czy dobrze " + path1);
////        System.out.println("a to " + file1.getParent());
////
////        // save the file on the local file system
////        try {
//////            Path path = Paths.get(UPLOAD_DIR + fileName);
////
////            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
////        dd
//
//
//        try (SpeechClient speech = SpeechClient.create()) {
//            // Configure request with video media type
//            RecognitionConfig recConfig =
//                    RecognitionConfig.newBuilder()
//                            // encoding may either be omitted or must match the value in the file header
//                            .setEncoding(RecognitionConfig.AudioEncoding.OGG_OPUS)
//                            .setLanguageCode("en-US")
//                            // sample rate hertz may be either be omitted or must match the value in the file
//                            // header
//                            .setSampleRateHertz(16000)
//                            .setModel("video")
//                            .build();
//
//            RecognitionAudio recognitionAudio =
//                    RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(content)).build();
//
//            RecognizeResponse recognizeResponse = speech.recognize(recConfig, recognitionAudio);
//            // Just print the first result here.
//            SpeechRecognitionResult result = recognizeResponse.getResultsList().get(0);
//            // There can be several alternative transcripts for a given chunk of speech. Just use the
//            // first (most likely) one here.
//            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//            System.out.printf("Transcript : %s\n", alternative.getTranscript());
//
//            model.addAttribute("transcription", "alternative.getTranscript()" );
//            fileText = alternative.getTranscript();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//
//        // return success response
//        attributes.addFlashAttribute("message", "You successfully uploaded " +file.getOriginalFilename() + '!');
//
//        return "redirect:/";
//    }
}
