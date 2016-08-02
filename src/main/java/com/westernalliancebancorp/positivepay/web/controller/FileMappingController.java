package com.westernalliancebancorp.positivepay.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Delimiter;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.service.DelimiterService;
import com.westernalliancebancorp.positivepay.service.FileMappingService;
import com.westernalliancebancorp.positivepay.service.UserService;

/**
 * Spring Controller to tackle file upload by customers
 * 
 * @author Anand Kumar
 * 
 */
@Controller
public class FileMappingController {

	@Loggable
	private Logger logger;
	
	@Autowired
	UserService userService;
	@Autowired
	FileMappingService fileMappingService;	
	@Autowired
	DelimiterService delimiterService;

	@RequestMapping(value = "/user/filemapping", method = RequestMethod.GET)
	public String showFileMappingPage(Model model, HttpServletRequest request)
			throws Exception {
		//Get All file mappings for logged in user
		List<FileMapping> fileMappings = fileMappingService.findAllForLoggedInUser();
		//Get All Delimiters
		List<Delimiter> delimiters = delimiterService.findAll();
		//Add models to be shown in the JSP
		model.addAttribute("delimiters", delimiters);
		model.addAttribute("fileMappings", fileMappings);
		return "site.file.mapping.page";
	}
	
	@RequestMapping(value = "/user/filemapping", method = RequestMethod.POST)
	public @ResponseBody FileMapping save(@RequestBody FileMapping fileMapping) {
		return fileMappingService.saveOrUpdate(fileMapping);
	}
	
	@RequestMapping(value = "/user/filemapping", method = RequestMethod.DELETE)
	public @ResponseBody boolean delete(@RequestBody FileMapping fileMapping) {
		fileMappingService.delete(fileMapping);
		return true;
	}
	@RequestMapping(value = "/user/filemapping/{id}", method = RequestMethod.GET)
	public @ResponseBody FileMapping get(@PathVariable Long id) {
		return fileMappingService.findById(id);
	}
}
