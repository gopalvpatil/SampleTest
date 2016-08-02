package com.westernalliancebancorp.positivepay.web.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.westernalliancebancorp.positivepay.dto.BankDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.CompanyDtoBuilder;
import com.westernalliancebancorp.positivepay.exception.HttpStatusCodedResponseException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Address;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CompanyService;

/**
 * 
 * @author umeshram
 *
 */
@Controller
public class BankController {
	
	@Loggable
	private Logger logger;
	
	@Autowired
	private BankService bankService;
	
	@Autowired
	private CompanyService companyService;
	
	@Value("${uploaded.images.location}")
	private String uploadedImagelocation;
	
	@RequestMapping(value="/user/managebanks")
	public String manageBanks(Model model, HttpServletRequest request) {
		List<Bank> banks = bankService.findAll();
		model.addAttribute("banks", banks);
		return "site.manage.banks.page";
	}
	
	@RequestMapping(value="/user/banks/{bankId}/companies")
	@ResponseBody
	public List<CompanyDTO> getAllCompanies(@PathVariable(value="bankId") Long bankId) throws Exception{
		try{
			List<CompanyDTO> companyList = getCompaniesList(bankId,true);
			return companyList;
		}catch(Exception ex) {
			logger.error("Error occurred while retrieving Company list for Bank Id :"+ bankId, ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
	
	@RequestMapping(value="/user/banks/{bankId}/companies/{companyId}")
	@ResponseBody
	public CompanyDTO getCompanyDetail(@PathVariable(value="bankId") Long bankId, @PathVariable(value="companyId") Long companyId) 
			throws HttpStatusCodedResponseException{
		try {
			Company company = companyService.getCompanyDetail(companyId);
			CompanyDTO companyDTO = CompanyDtoBuilder.getCompanyDtoFromEntity(company);
			return companyDTO;
		} catch(Exception ex) {
			logger.error("Error occurred while retrieving Company detail for company Id :" + companyId, ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

		}
	}
	
	@RequestMapping(value = "/user/banksetup", method=RequestMethod.GET)
	public String bankDetails(Model model, @RequestParam(required = false, value = "bankId") String bankId, HttpServletRequest request)
			throws Exception {
		BankDto bankDto = new BankDto();
		if (StringUtils.isNotBlank(bankId)) {
			Bank  bank = bankService.findById(Long.parseLong(bankId));
			bankDto.setId(bank.getId());
			bankDto.setBankName(bank.getName());
			bankDto.setBankId(bank.getAssignedBankNumber());
			bankDto.setBankNumber(bank.getRoutingNumber());
			bankDto.setLogoPathFilename(bank.getLogoPathFilename());
			bankDto.setWebsiteUrl(bank.getWebsiteUrl());
			if(bank.getAddress() != null) {
				bankDto.setStreetAddress(bank.getAddress().getAddress1());
				bankDto.setStreetAddress2(bank.getAddress().getAddress2());
				bankDto.setState(bank.getAddress().getState());
				bankDto.setCity(bank.getAddress().getCity());
				bankDto.setZipCode(bank.getAddress().getZipCode());
			}
			bankDto.setCompanies(getCompaniesList(Long.parseLong(bankId),false));
		}
		model.addAttribute("bankDto", bankDto);
		return "site.bank.setup.page";
	}
	
	@RequestMapping(value = "/user/banksetup", method=RequestMethod.POST)
	public String saveBank(Model model, @ModelAttribute("bankDto") BankDto bankDto, final BindingResult bindingResult,
			@RequestParam(required=false,value="bankLogo") MultipartFile file,
			@RequestParam(required=false,value="redirectUrl") String redirectUrl,
			final RedirectAttributes redirectAttributes, HttpServletRequest request)
			throws Exception {
		
		//First save the bank logo to avoid any errors afterwards
		if (file!= null && !file.isEmpty()) {
			String newFile = handleFileUpload(file, model);//Upload file to Banklogo directory
			if(StringUtils.isNotBlank(newFile))
				bankDto.setLogoPathFilename(newFile);//Override the logo path name
		}
		
		if (bindingResult.hasErrors()) {
	        return "site.bank.setup.page";
	    }
		
		if (bankDto.getBankId() != null) {
			Bank otherBank = bankService.findByAssignedBankNumber(bankDto.getBankId());
			if(otherBank != null) {
				if(bankDto.getId() == null || !bankDto.getId().equals(otherBank.getId())) {
					bindingResult.rejectValue("bankId", null, 
							"Bank Id is already assigned to another bank. Please correct and submit again.");
					return "site.bank.setup.page";
				}
			}
		}
		
		try {
			Bank bank = null;
			if (bankDto.getId() != null) {
				bank = bankService.findById(bankDto.getId());
			} else {
				bank = new Bank();
				bank.setActive(true);
			}
			
			bank.setLogoPathFilename(bankDto.getLogoPathFilename());
			bank.setName(bankDto.getBankName());
			bank.setAssignedBankNumber(bankDto.getBankId());
			bank.setRoutingNumber(bankDto.getBankNumber());
			bank.setWebsiteUrl(bankDto.getWebsiteUrl());
			
			Address address = new Address();
			address.setAddress1(bankDto.getStreetAddress());
			address.setAddress2(bankDto.getStreetAddress2());
			address.setCity(bankDto.getCity());
			address.setState(bankDto.getState());
			address.setZipCode(bankDto.getZipCode());
			bank.setAddress(address);
			
			bank = bankService.saveOrUpdate(bank);
			
			if(StringUtils.isBlank(redirectUrl)) {
				redirectAttributes.addFlashAttribute("successMessage", "Bank details has been saved succesfully !!");
				return "redirect:banksetup?bankId="+bank.getId();
			} else {
				// Hack !! Redirect to Add Company or Edit Company. 
				// Append bankId as in case of New bank it will come empty from UI.
				return "redirect:" + redirectUrl + (bankDto.getId() == null ? bank.getId() : bankDto.getId());
			}
		} catch(Exception e) {
			logger.error("Error occurred while storing bank information ", e);
			throw e;
		}
	}
	
	@RequestMapping(value = "/user/banksetup/delete/company")
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteCompany(@RequestBody CompanyDTO companyDTO) throws Exception {
		try{
			companyService.makeCompanyAndItsAccountInactive(companyDTO.getId());
		}catch(Exception ex) {
			logger.error("Error occurred while deleting Company for company Id :"+ companyDTO.getId(), ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

		}

	}
	
	@RequestMapping(value = "/user/bank/logo", method = RequestMethod.POST)
	@ResponseBody
	public String handleFileUpload( @RequestParam("logoImage") MultipartFile file,
	                Model model) throws Exception {
		 if (file!= null && !file.isEmpty()) {
			 try{
				 File directory = new File(uploadedImagelocation);
				 if (!directory.exists()) {directory.mkdirs();}
			     String newFileName = String.valueOf(UUID.randomUUID()) + "." + FilenameUtils.getExtension(file.getOriginalFilename());
			      
			     BufferedImage src = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
			     File destination = new File(uploadedImagelocation+newFileName);
			     ImageIO.write(src, "png", destination);
			     return newFileName;
			 }catch(Exception e) {
				 logger.error("Error occurred while uploading bank logo", e);
				 throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			 }
		 }
		return null;
	}
	
	@RequestMapping(value = "/user/bank/logo/{logoPathFilename}.{extension}", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> bankLogo(@PathVariable String logoPathFilename,@PathVariable String extension)  throws Exception{
		if(StringUtils.isNotBlank(logoPathFilename)) {
			extension =  StringUtils.isBlank(extension)?"":"."+extension.trim();
			File fileToRead = new File(uploadedImagelocation+logoPathFilename+extension);
			if(!fileToRead.exists()) {
				logger.error(String.format("Bank logo filename %s is not found",logoPathFilename));
				throw new HttpStatusCodedResponseException(HttpStatus.NOT_FOUND, "Bank logo not found");
			}
			try {
		        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(fileToRead),HttpStatus.OK);
			}catch (IOException e) {
				logger.error(String.format("Error occurred while returning Bank logo %s",logoPathFilename),e);
				throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		return null;
	}
	
	private List<CompanyDTO> getCompaniesList(Long bankId, boolean ignoreInactiveCompanies) throws Exception {
		List<Long> bankIds = new ArrayList<Long>();
		bankIds.add(bankId);
		List<Company> companies = companyService.findAllByBankIds(bankIds);
		List<CompanyDTO> companyDTOs = new ArrayList<CompanyDTO>();
		
		for(Company company : companies) {
			if(!company.isActive() && ignoreInactiveCompanies)
				continue;
			CompanyDTO companyDTO = new CompanyDTO();
			companyDTO.setId(company.getId());
			companyDTO.setName(company.getName());
			companyDTO.setActive(company.isActive());
			companyDTOs.add(companyDTO);
		}
		
		return companyDTOs;
	}
	
}

