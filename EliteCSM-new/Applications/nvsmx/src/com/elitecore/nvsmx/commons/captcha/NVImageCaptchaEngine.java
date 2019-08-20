package com.elitecore.nvsmx.commons.captcha;

import java.awt.Color;
import java.awt.Font;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.gimpy.SimpleListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

public class NVImageCaptchaEngine extends SimpleListImageCaptchaEngine{

	@Override
	protected void buildInitialFactories() {
		WordGenerator wgen = new RandomWordGenerator("abcdefghijklmnopqrstuvwxyz123456789");
		RandomRangeColorGenerator cgen = new RandomRangeColorGenerator(new int[] { 0, 255 }, new int[] { 20, 100 }, new int[] { 20, 100 });

		TextPaster textPaster = new RandomTextPaster(new Integer(4), new Integer(5), cgen, Boolean.TRUE);

		BackgroundGenerator backgroundGenerator = new UniColorBackgroundGenerator(new Integer(240), new Integer(50), new Color(252,252,253));
		Font[] fontsList = new Font[] { new Font("Helvetica", Font.TYPE1_FONT, 10), new Font("Arial", 0, 14), new Font("Vardana", 0, 17) };

		FontGenerator fontGenerator = new RandomFontGenerator(new Integer(18), new Integer(30), fontsList);
		WordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
		this.addFactory(new GimpyFactory(wgen, wordToImage));
	}

}
