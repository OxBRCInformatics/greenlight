package uk.ac.ox.brc.greenlight

/**
 * Created by soheil on 28/07/2015.
 */
class GELBarcodeParserService {


	public final static char GS = (char) 29;
	public final static char PIPE = (char) 124; //pipe character |
	public final static char TILDE = (char) 126; //tilde character ~

	public final static String GEL_GS1_ORGANISATION_PREFIX = "0000000"

	def parseGELBarcodeString(String barcodeString) {

		def parts = barcodeString?.tokenize(GS)
		if (parts.size() != 5) {
			parts = barcodeString?.tokenize("↔")
			if (parts.size() != 5) {
				return [
						error  : "GEL 2D Barcode should have five sections each separated by a GS character",
						success: false,
						result : [:]
				]
			}
		}

		//Find GEL form type
		def slfDetailsStr = parts[4].substring(2, parts[4].length())
		def slfDetails = slfDetailsStr.split(/\|/, -1)
		if (slfDetails.size() != 2) {
			slfDetails = slfDetailsStr.split(/\~/, -1)
			if (slfDetails.size() != 2) {
				return [
						error  : "GEL 2D Barcode SLF Detail should contain two sections (version,date)",
						success: false,
						result : [:]
				]
			}
		}

		//GSRN & NHSNumber
		if (!parts[0].startsWith("8018")) {
			return [
					error  : "GEL 2D Barcode GSRN section should start with 8018",
					success: false,
					result : [:]
			]
		}

		//DiseaseType
		if (!parts[3].startsWith("93")) {
			return [
					error  : "GEL 2D Barcode Disease Type should start with 93",
					success: false,
					result : [:]
			]
		}
		def diseaseType = parts[3].substring(2, parts[3].length())

		//it is Rare Disease
		if (diseaseType.toLowerCase().contains("rare")) {
			return parseGELRareDisease(barcodeString)
		} else if (diseaseType.toLowerCase().contains("cancer")) {
			return parseGELCancer(barcodeString)
		}

		return [
				error  : "Can not find the parser for this type of GEL Form",
				success: false,
				result : [:]
		]

	}

	private parseGELRareDisease(String barcodeString) {
		def parts = barcodeString?.tokenize(GS)
		if (parts.size() != 5) {
			parts = barcodeString?.tokenize("↔")
			if (parts.size() != 5) {
				return [
						error  : "GEL 2D Barcode should have five sections each separated by a GS character",
						success: false,
						result : [:]
				]
			}
		}

		//Find GEL form type
		def slfDetailsStr = parts[4].substring(2, parts[4].length())
		def slfDetails = slfDetailsStr.split(/\|/, -1)
		if (slfDetails.size() != 2) {
			slfDetails = slfDetailsStr.split(/\~/, -1)
			if (slfDetails.size() != 2) {
				return [
						error  : "GEL 2D Barcode SLF Detail should contain two sections (version,date)",
						success: false,
						result : [:]
				]
			}
		}

		//ignore "8018XXXXXXX1234567890X" and also the Checksum at the end and just get the NHSNumber
		def NHSNumber = parts[0].substring(11, parts[0].length() - 1)

		//participantId
		if (!parts[1].startsWith("91")) {
			return [
					error  : "GEL 2D Barcode Participant Id should start with 91",
					success: false,
					result : [:]
			]
		}
		def participantId = parts[1].substring(2, parts[1].length())

		//participantDetails
		if (!parts[2].startsWith("92")) {
			return [
					error  : "GEL 2D Barcode Participant Details should start with 92",
					success: false,
					result : [:]
			]
		}
		def partDetailStr = parts[2].substring(2, parts[2].length())

		//First try to split based on PIPE, if not then TILDE, add -1 into split as we might have empty values
		def participantDetails = partDetailStr.split(/\|/, -1)
		if (participantDetails.size() != 6) {
			participantDetails = partDetailStr.split(/~/, -1)
			if (participantDetails.size() != 6) {
				return [
						error  : "GEL 2D Barcode Participant Details should contain six sections",
						success: false,
						result : [:]
				]
			}
		}

		//SLF version and date
		if (!parts[4].startsWith("94")) {
			return [
					error  : "GEL 2D Barcode SLF version and date should start with 94",
					success: false,
					result : [:]
			]
		}


		//"23/07/1985"
		Date date = Date.parse( 'dd/MM/yyyy', participantDetails[5] )
		String newDate = date.format( 'd/M/yyyy' ) // we need Date as 2/7/1985
		def dob = newDate.split("/")

		[
				error  : "",
				success: true,
				result : [
						NHSNumber         : NHSNumber,
						participantId     : participantId,
						participantDetails: [
								hospitalNumber: participantDetails[2],
								forenames     : participantDetails[3],
								surname       : participantDetails[4],
								dateOfBirth   : participantDetails[5],
								dobYear:  dob[2],
								dobMonth: dob[1],
								dobDate:  dob[0]
						],
						diseaseType       : "Rare Disease",
						SLF               : [
								version: slfDetails[0],
								date   : slfDetails[1]
						]
				]
		]
	}

	private parseGELCancer(String barcodeString) {
		def parts = barcodeString?.tokenize(GS)
		if (parts.size() != 5) {
			parts = barcodeString?.tokenize("↔")
			if (parts.size() != 5) {
				return [
						error  : "GEL 2D Barcode should have five sections each separated by a GS character",
						success: false,
						result : [:]
				]
			}
		}

		//Find GEL form type
		def slfDetailsStr = parts[4].substring(2, parts[4].length())
		def slfDetails = slfDetailsStr.split(/\|/, -1)
		if (slfDetails.size() != 2) {
			slfDetails = slfDetailsStr.split(/\~/, -1)
			if (slfDetails.size() != 2) {
				return [
						error  : "GEL 2D Barcode SLF Detail should contain two sections (version,date)",
						success: false,
						result : [:]
				]
			}
		}

		//ignore "8018XXXXXXX1234567890X" and also the Checksum at the end and just get the NHSNumber
		def NHSNumber = parts[0].substring(11, parts[0].length() - 1)

		//participantId
		if (!parts[1].startsWith("91")) {
			return [
					error  : "GEL 2D Barcode Participant Id should start with 91",
					success: false,
					result : [:]
			]
		}
		def participantId = parts[1].substring(2, parts[1].length())

		//participantDetails
		if (!parts[2].startsWith("92")) {
			return [
					error  : "GEL 2D Barcode Participant Details should start with 92",
					success: false,
					result : [:]
			]
		}
		def partDetailStr = parts[2].substring(2, parts[2].length())

		//First try to split based on PIPE, if not then TILDE, add -1 into split as we might have empty values
		def participantDetails = partDetailStr.split(/\|/, -1)
		if (participantDetails.size() != 5) {
			participantDetails = partDetailStr.split(/~/, -1)
			if (participantDetails.size() != 5) {
				return [
						error  : "GEL 2D Barcode Participant Details should contain five sections",
						success: false,
						result : [:]
				]
			}
		}

		//SLF version and date
		if (!parts[4].startsWith("94")) {
			return [
					error  : "GEL 2D Barcode SLF version and date should start with 94",
					success: false,
					result : [:]
			]
		}



		//"02/07/1985"
		Date date = Date.parse( 'dd/MM/yyyy', participantDetails[4] )
		String newDate = date.format( 'd/M/yyyy' ) // we need Date as 2/7/1985
		def dob = newDate.split("/")

		[
				error  : "",
				success: true,
				result : [
						NHSNumber         : NHSNumber,
						participantId     : participantId,
						participantDetails: [
								hospitalNumber: participantDetails[1],
								forenames     : participantDetails[2],
								surname       : participantDetails[3],
								dateOfBirth   : participantDetails[4],
								dobYear:  dob[2],
								dobMonth: dob[1],
								dobDate:  dob[0]
						],
						diseaseType       : "Cancer",
						SLF               : [
								version: slfDetails[0],
								date   : slfDetails[1]
						]
				]
		]
	}


}

//public String getSLFDatamatrixBarcodeString(Participant participant) throws Exception{
//
//	ArrayList<GS1Element> GS1Codes = new ArrayList<GS1Element>();
//
//	String checkDigit = GS1Formatter.GRSNCheckDigit(GS1Formatter.GEL_GS1_ORGANISATION_PREFIX + participant.nhsNumber);
//	//based on http://www.isb.nhs.uk/documents/isb-1077/amd-144-2010/10771442010spec.pdf
//	//GSRN consists of GEL_GS1_ORGANISATION_PREFIX + nhsNumber + checkDigit
//	GS1Element gsElement = new GS1Element("8018","GEL_GSRN_and_nhsNumber", GS1Formatter.GEL_GS1_ORGANISATION_PREFIX + participant.nhsNumber + checkDigit);
//	GS1Codes.add(gsElement);
//
//	GS1Codes.add(new GS1Element("91","participantId", GS1Formatter.removeChars(participant.participantId) ));
//	String participantDetails="";
//	switch (slfType){
//		case RARE_DISEASES:{
//			//consists of familyId|clinicId|hospitalNumber|forenames|surname|dob
//			participantDetails = GS1Formatter.removeChars(participant.familyId)  + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.clinicId)  + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.hospitalNumber) + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.forenames) + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.surname)   + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.dateOfBirth);
//
//		} break;
//		case CANCER_BLOOD:{
//			//consists of clinicId|hospitalNumber|forenames|surname|dob
//			participantDetails =  GS1Formatter.removeChars(participant.clinicId)  + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.hospitalNumber) + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.forenames) + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.surname)   + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.dateOfBirth);
//
//		} break;
//		case CANCER_TISSUE:{
//			//consists of hospitalSiteCode|hospitalNumber|forenames|surname|dob
//			participantDetails = GS1Formatter.removeChars(participant.hospitalSiteCode)  + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.hospitalNumber) + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.forenames) + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.surname)   + GS1Formatter.PIPE +""
//			+  GS1Formatter.removeChars(participant.dateOfBirth);
//		} break;
//	}
//
//
//	GS1Codes.add(new GS1Element("92","participantDetails",participantDetails));
//	GS1Codes.add(new GS1Element("93","diseaseType",participant.diseaseType));
//	GS1Codes.add(new GS1Element("94","slf",slfType.getVersion() + GS1Formatter.PIPE + slfType.getDate()));
//
//	//For example, the following code means ( we do not add open/close paranthes in the string in the actual system):
//	//(91)1234567890(92)12345(93)100|290|SMITH|John|28/02/2015(94)Rare Disease(95)DNA Blood Germline
//	//5 is checkDigit
//	//8018000000012345678905<GS>9112345<GS>92100|290|SMITH|John|28/02/2015<GS>93Rare Disease<GS>94DNA Blood Germline
//
//	//nshNumber: 1234567890
//	//surname: SMITH
//	//forenames: John
//	//dob: 01/02/2015
//	//particpantId: 12345
//	//familyId: 100
//	//clinicId: 290
//	//diseaseType: Rare Disease
//	//sampleType: DNA Blood Germline
//	return GS1Formatter.getBarcodeString(GS1Codes);
//}
