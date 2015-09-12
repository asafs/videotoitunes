package com.mastra;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.apple.AppleCoverBox;
import com.coremedia.iso.boxes.apple.AppleDescriptionBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.apple.AppleShowBox;
import com.coremedia.iso.boxes.apple.AppleTrackTitleBox;
import com.coremedia.iso.boxes.apple.AppleTvEpisodeBox;
import com.coremedia.iso.boxes.apple.AppleTvEpisodeNumberBox;
import com.coremedia.iso.boxes.apple.AppleTvSeasonBox;

public class AddAppleSeriesData {

	private SeriesParsing series = null;
	private EpisodeData selectedEpisode = null;
	private IsoFile isoFile;
	
	public AddAppleSeriesData(){}
	
	public void parseSeriesData(String SeriesName) throws IOException{
		
		series = new SeriesParsing();
		series.parse(SeriesName);
	}
	
	public void selectEpisode(int nSeason, int nEpisode){
		if (series == null)
			return;
		
		selectedEpisode = series.getEpisode(nSeason, nEpisode);
	}

	public void parseFile(String fileName, String newFileName) throws IOException{
		// open Iso File
		
		
		FileInputStream fis = new FileInputStream(fileName);
		FileChannel fc = fis.getChannel();
		isoFile = new IsoFile(fc);

		// get Apple Item List Box
		//List<Box> boxes = isoFile.getBoxes();
		MovieBox moov = isoFile.getBoxes(MovieBox.class).get(0);
		System.out.println("num of boxes in iso file = " + isoFile.getBoxes().size());
		System.out.println("num of moov boxes in iso file = " + isoFile.getBoxes(MovieBox.class).size());
		// get User Data Box
		UserDataBox udta;
		List<UserDataBox> moovBoxes = moov.getBoxes(UserDataBox.class);
		System.out.println("num of boxes in moov = " + moov.getBoxes().size());
		System.out.println("num of user data boxes in moov = " + moovBoxes.size());
		if (moovBoxes.size() > 0)
			udta = moovBoxes.get(0);
		else
			udta = new UserDataBox();
			
				
		// get MetaBox
		MetaBox meta;
		List<MetaBox> udtaBoxes = udta.getBoxes(MetaBox.class);
		System.out.println("num of boxes in udta = " + udta.getBoxes().size());
		System.out.println("num of meta boxes in udta = " + udtaBoxes.size());
		if (udtaBoxes.size() > 0)
			meta = udtaBoxes.get(0);
		else
			meta = new MetaBox();
		
		AppleItemListBox apl;
		System.out.println("num of boxes in meta = " + meta.getBoxes().size());
		System.out.println("num of Apple item boxes in meta = " + meta.getBoxes(AppleItemListBox.class).size());
		if (meta.getBoxes(AppleItemListBox.class).size() > 0)
			apl = meta.getBoxes(AppleItemListBox.class).get(0);
		else
			apl = new AppleItemListBox();
		// make changes to apple items

		AppleItemListBox newApl = new AppleItemListBox();

		// add series name - AppleShowBox
		AppleShowBox aplShowBox = new AppleShowBox();
		aplShowBox.setValue(series.getSeriesData().getSeriesName());
		newApl.addBox(aplShowBox);

		AppleDescriptionBox aplDescriptionBox = new AppleDescriptionBox();
		aplDescriptionBox.setValue(selectedEpisode.getEpisodeOverview());
		newApl.addBox(aplDescriptionBox);

		AppleTvEpisodeNumberBox aplTvEpisodeNumberBox = new AppleTvEpisodeNumberBox();
		aplTvEpisodeNumberBox.setValue(selectedEpisode.getEpisodeName());
		newApl.addBox(aplTvEpisodeNumberBox);

		AppleTvSeasonBox aplTvSeasonBox = new AppleTvSeasonBox();
		aplTvSeasonBox.setValue(selectedEpisode.getSeasonNumber());
		newApl.addBox(aplTvSeasonBox);

		AppleTvEpisodeBox aplTvEpisodeBox = new AppleTvEpisodeBox();
		aplTvEpisodeBox.setValue(selectedEpisode.getEpisodeNumber());
		newApl.addBox(aplTvEpisodeBox);

		AppleTrackTitleBox aplTrackTitleBox = new AppleTrackTitleBox();
		aplTrackTitleBox.setValue(selectedEpisode.getEpisodeName());
		newApl.addBox(aplTrackTitleBox);

		// add image - not working
		AppleCoverBox aplCoverBox = new AppleCoverBox();
		aplCoverBox.setJpg(readJpgData(series.getSeriesData().getPosterURL()));
		newApl.addBox(aplCoverBox);


		// create new iso file
		
		//
		//HandlerBox hdlr = new HandlerBox();
		//hdlr.setHandlerType("mdir");
		//hdlr.setName("");
		//System.out.println(hdlr);
		//meta = (MetaBox) addOrReplaceBox(meta, "hdlr", hdlr);
		meta = (MetaBox) addOrReplaceBox(meta, "ilst", newApl);
		//meta = (MetaBox) addOrReplaceBox(meta, "ilst", apl);
		
		
		udta = (UserDataBox) addOrReplaceBox(udta, "meta", meta);

		moov = (MovieBox) addOrReplaceBox(moov, "udta", udta);

		isoFile = (IsoFile) addOrReplaceBox(isoFile, "moov", moov);
		
		
		System.out.println("**********NEW PRINT*************");
		moov = isoFile.getBoxes(MovieBox.class).get(0);
		System.out.println("num of boxes in iso file = " + isoFile.getBoxes().size());
		System.out.println("num of moov boxes in iso file = " + isoFile.getBoxes(MovieBox.class).size());
		// get User Data Box
		moovBoxes = moov.getBoxes(UserDataBox.class);
		System.out.println("num of boxes in moov = " + moov.getBoxes().size());
		System.out.println("num of user data boxes in moov = " + moovBoxes.size());
		if (moovBoxes.size() > 0)
			udta = moovBoxes.get(0);
		else
			udta = new UserDataBox();
		
		
		// get MetaBox
		udtaBoxes = udta.getBoxes(MetaBox.class);
		System.out.println("num of boxes in udta = " + udta.getBoxes().size());
		System.out.println("num of meta boxes in udta = " + udtaBoxes.size());
		if (udtaBoxes.size() > 0)
			meta = udtaBoxes.get(0);
		else
			meta = new MetaBox();
		
		System.out.println(meta);
		System.out.println(meta.getBoxes(HandlerBox.class));

		
		System.out.println("num of boxes in meta = " + meta.getBoxes().size());
		System.out.println("num of Apple item boxes in meta = " + meta.getBoxes(AppleItemListBox.class).size());
		if (meta.getBoxes(AppleItemListBox.class).size() > 0)
			apl = meta.getBoxes(AppleItemListBox.class).get(0);
		else
			apl = new AppleItemListBox();
		

		// save Iso File
		FileOutputStream fos = new FileOutputStream(newFileName);
		isoFile.getBox(fos.getChannel());
		
		isoFile.close();
		fis.close();
		fc.close();	
		fos.close();
		
	}
	
	private Box addOrReplaceBox(ContainerBox parentBox, String childBoxType, Box newChildBox){
		List<Box> boxes = parentBox.getBoxes();
		List<Box> newListBoxes = new LinkedList<Box>();
		Iterator<Box> iter = boxes.iterator();
		boolean didExist = false;
		while (iter.hasNext()){
			Box b = iter.next();
			if (b.getType() == childBoxType){
				newListBoxes.add(newChildBox);
				didExist = true;
			}
			else{
				newListBoxes.add(b);
			}
		}
		if (!didExist){
			newListBoxes.add(newChildBox);
		}
		parentBox.setBoxes(newListBoxes);
		return parentBox;
	}
	
	private byte[] readJpgData(String url) throws IOException{
		BufferedImage img = null;
		img = ImageIO.read(new URL(url));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", baos);
		baos.flush();
		return baos.toByteArray();
	}
}
