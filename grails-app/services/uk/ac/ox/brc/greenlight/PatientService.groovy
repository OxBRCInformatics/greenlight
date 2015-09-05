package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class PatientService {

	/**
	 * Find all patients by their NHS or hospital numbers.
	 * As we have patient per consent, so there may be more than one patient object for an NHS or MRN number
	 * @param id The NHS number or Hospital Number
	 * @return The patients list, or null if there was no match
	 */
	def findAllByNHSOrHospitalNumber(String patientNumber) {
		return Patient.findAllByNhsNumberOrHospitalNumber(patientNumber, patientNumber)
	}


	def groupPatientsByNHSNumber(){
		def c = Patient.createCriteria()
		c.list {
			projections {
				groupProperty("nhsNumber")
			}
			order("nhsNumber")
		}
	}

	def groupPatientsByHospitalNumber() {

		Patient.createCriteria().list {
			projections {
				groupProperty("hospitalNumber")
			}
		}
	}


	def findPatientWithGenericNHSNumber(){

		def c = Patient.createCriteria()
		c.list {
			and{
				eq("nhsNumber","1111111111")
			}
			projections {
				groupProperty("hospitalNumber")
			}
 		}
	}

	def isGenericNHSNumber(nhsNumber){
		return nhsNumber == "1111111111"
	}

}
