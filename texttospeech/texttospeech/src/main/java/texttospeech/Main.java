package texttospeech;

import java.io.FileOutputStream;
import java.io.OutputStream;

import com.google.cloud.texttospeech.v1beta1.AudioConfig;
import com.google.cloud.texttospeech.v1beta1.AudioEncoding;
import com.google.cloud.texttospeech.v1beta1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1beta1.SynthesisInput;
import com.google.cloud.texttospeech.v1beta1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1beta1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1beta1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

public class Main {

	public static void main(String[] args) throws Exception {
		synthesizeText(args[0]);

	}

	/**
	 * Demonstrates using the Text to Speech client to synthesize text or ssml.
	 * @param text the raw text to be synthesized. (e.g., "Hello there!")
	 * @throws Exception on TextToSpeechClient Errors.
	 */
	public static void synthesizeText(String text)
	    throws Exception {
	  // Instantiates a client
	  try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
	    // Set the text input to be synthesized
	    SynthesisInput input = SynthesisInput.newBuilder()
	        .setText(text)
	        .build();

	    // Build the voice request
	    VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
	        .setLanguageCode("en-US") // languageCode = "en_us"
	        .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
	        .build();

	    // Select the type of audio file you want returned
	    AudioConfig audioConfig = AudioConfig.newBuilder()
	        .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
	        .build();

	    // Perform the text-to-speech request
	    SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
	        audioConfig);

	    // Get the audio contents from the response
	    ByteString audioContents = response.getAudioContent();

	    // Write the response to the output file.
	    try (OutputStream out = new FileOutputStream("output.mp3")) {
	      out.write(audioContents.toByteArray());
	      System.out.println("Audio content written to file \"output.mp3\"");
	    }
	  }
	}
}
