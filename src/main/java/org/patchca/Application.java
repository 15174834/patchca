package org.patchca;

import java.awt.Color;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.filter.predefined.DiffuseRippleFilterFactory;
import org.patchca.filter.predefined.DoubleRippleFilterFactory;
import org.patchca.filter.predefined.MarbleRippleFilterFactory;
import org.patchca.filter.predefined.WobbleRippleFilterFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.word.RandomWordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import me.douboo.springboot.memcached.EnableMemcached;
import net.spy.memcached.MemcachedClient;

@Controller
@SpringBootApplication
@EnableMemcached
public class Application {
	private final static Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	private MemcachedClient memcachedClient;

	private static final String PATCHA_CODE_KEY = "PATCHA_CODE_KEY_";

	@RequestMapping({ "", "/hcaptcha/api/image" })
	@ResponseBody
	void img(@RequestParam String hcaptcha_token,
			@RequestParam(required = false, defaultValue = "refresh") String hcaptcha_opt, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
			cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
			int c = (int) (System.currentTimeMillis() % 5);
			switch (c) {
			case 0:
				cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
				break;
			case 1:
				cs.setFilterFactory(new MarbleRippleFilterFactory());
				break;
			case 2:
				cs.setFilterFactory(new DoubleRippleFilterFactory());
				break;
			case 3:
				cs.setFilterFactory(new WobbleRippleFilterFactory());
				break;
			case 4:
				cs.setFilterFactory(new DiffuseRippleFilterFactory());
				break;
			}

			cs.setHeight(60);
			cs.setWidth(120);
			cs.setWordFactory(new RandomWordFactory("2345678abcdegikpsvxyz", 4, 4));
			Captcha captcha = cs.getCaptcha();
			boolean write = ImageIO.write(captcha.getImage(), "PNG", response.getOutputStream());
			if (write) {
				this.memcachedClient.set(PATCHA_CODE_KEY + hcaptcha_token, 300, captcha.getChallenge());
			}
			logger.debug("gen image {},key = {} , code = {}", write, hcaptcha_token, captcha.getChallenge());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "hcaptcha/api/verify" })
	@ResponseBody
	boolean verify(@RequestParam String hcaptcha_token, @RequestParam String hcaptcha_word, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Object object = this.memcachedClient.get(PATCHA_CODE_KEY + hcaptcha_token);
			if (null != object && object.equals(hcaptcha_word)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.memcachedClient.delete(PATCHA_CODE_KEY + hcaptcha_token);
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}