package com.mastra;

import java.io.File;
import java.io.IOException;

public class AddMp4Data {

	static String fileType;// type = movie or series 
	static String fileName;
	static String newFileName;
	static String videoName;
	static int nSeason;
	static int nEpisode;

	public static void main(String[] args) {

		//parseInput(args);
		//fileName = "output.m4v";
		fileName = "f.m4v";
		fileType = "movie";		
		newFileName = fileName.substring(0, fileName.length()-4) + "_new." + fileName.substring(fileName.length()-3, fileName.length());
		System.out.println(newFileName);
		videoName = "The Matrix";
		nSeason = 9;
		nEpisode = 1;

		if (fileType.compareTo("series") == 0){
			try {
				System.out.println("Starting to parse file:");
				AddAppleSeriesData mp4 = new AddAppleSeriesData();
				mp4.parseSeriesData(videoName);
				mp4.selectEpisode(nSeason, nEpisode);				
				mp4.parseFile(fileName, newFileName);
				System.out.println("Finished parsing file");
			} catch (IOException e) {
				System.out.println("Problem with IO. Error Stack:");
				e.printStackTrace();
			}
		} else if (fileType.compareTo("movie") == 0){
			MovieParsing mov = new MovieParsing();
			try {
				mov.parse(videoName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Error with file type");
		}

	}

	public static void parseInput(String[] args){
		if (args.length > 0){
			fileType = args[0];
		}	
		if (args.length > 1){
			fileName = args[1];
			File f = new File(fileName);
			if (!f.exists()){
				System.out.println("File doesn't exist. Exiting...");
				System.exit(1);
			}
			newFileName = fileName.substring(0, fileName.length()-4) + "_new.m4v";
			System.out.println("New File:\n" + newFileName);
		}
		else{
			System.out.println("Must have input file or directory. Exiting...");
			System.exit(1);
		}
		if (args.length > 2){
			videoName = args[2];
		}
		else{
			System.out.println("Must have input video name. Exiting...");
		}
		if (args.length > 3)
			nSeason = Integer.parseInt(args[3]);
		if (args.length > 4)
			nEpisode = Integer.parseInt(args[4]);
	}

}
