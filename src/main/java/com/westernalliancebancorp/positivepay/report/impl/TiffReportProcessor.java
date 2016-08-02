package com.westernalliancebancorp.positivepay.report.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;

import com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;
import com.westernalliancebancorp.positivepay.report.AbstractReportProcessor;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * User:	Gopal Patil
 * Date:	Feb 19, 2014
 * Time:	6:49:32 PM
 */
public class TiffReportProcessor extends AbstractReportProcessor {
	@Override
	public void exportReport(JasperPrint jp, ByteArrayOutputStream baos) {		 
		try {
			BufferedImage image = new BufferedImage(jp.getPageWidth(), jp.getPageHeight(), BufferedImage.TYPE_INT_RGB);

			//ImageIO.write(image, "tiff", baos);
			//ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
			
			//Graphics2D graphics = ImageIO.read(inputStream).createGraphics();
			Graphics2D graphics = image.createGraphics();
			// Create a JRXlsExporter instance
			JRGraphics2DExporter exporter = new JRGraphics2DExporter();
			 
			// Here we assign the parameters jp and baos to the exporter
			exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, graphics);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);

			exporter.exportReport();	
			
		    IIORegistry registry = IIORegistry.getDefaultInstance();  
		    registry.registerServiceProvider(new TIFFImageWriterSpi());  
		    registry.registerServiceProvider(new TIFFImageReaderSpi());  
		    
			ImageIO.write(image, "tiff", baos);
		} catch (JRException e) {
			throw new RuntimeException("Error occurred exporting TIFF report ", e);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void writeReport(HttpServletResponse response,
			ByteArrayOutputStream baos, String templateName) {
		super.writeResponse(response, baos, templateName, Constants.MEDIA_TYPE_TIFF, Constants.FILE_EXTENSION_TIFF);	
	}
}
