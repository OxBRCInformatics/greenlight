package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import groovy.json.JsonSlurper
import spock.lang.Specification

/**
 * Created by soheil on 28/07/2015.
 */
@TestFor(GELBarcodeParserService)
class GELBarcodeParserServiceSpec extends Specification{

	def GS = GELBarcodeParserService.GS

	def "parseGELBarcodeString returns participant details for input GEL Rare Disease Barcode String"(){

		given:
		File file = new File("test/resources/GELBarcodeRareDiseases.json")
		def barcodes = new JsonSlurper().parse(file)

		expect:
		barcodes.each{ barcode ->
			log.println("Testing GELBarcode ${barcode.inputBarcode}")
			def actual = service.parseGELBarcodeString(barcode.inputBarcode)
			assert actual == barcode.result
		}
	}

}
