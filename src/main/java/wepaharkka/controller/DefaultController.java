package wepaharkka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class DefaultController {

	@RequestMapping()
	@ResponseBody
	public String hehee() {
		return "lol";
	}
}
