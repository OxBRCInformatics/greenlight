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

}
