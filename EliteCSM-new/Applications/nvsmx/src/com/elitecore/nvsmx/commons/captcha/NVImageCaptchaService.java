package com.elitecore.nvsmx.commons.captcha;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

/*
 * NVImage Captcha service will generate a captcha image in accordance to the registered Captcha Engine. 
 * @author aneri.chavda
 */
public class NVImageCaptchaService extends GenericManageableCaptchaService{

	
	public NVImageCaptchaService(){
		super(new FastHashMapCaptchaStore(), new NVImageCaptchaEngine(), 180,
				100000, 75000); 
	}
	@Override
	protected Object getChallengeClone(Captcha captcha) {
		BufferedImage challenge = (BufferedImage) captcha.getChallenge();
		BufferedImage clone = new BufferedImage(200, 50, challenge.getType());
		Graphics2D graphics =  (Graphics2D) clone.getGraphics();
		graphics.drawImage(challenge, 0, 0, 200, 50, null);
		clone.getGraphics().dispose(); 
		return clone;
	}

}
