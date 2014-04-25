package uk.ac.ox.brc.greenlight.marshaller


/**
 * Created by soheil on 09/04/2014.
 */
class CustomObjectMarshallers {

    List marshallers = []

    def register() {
        marshallers.each{ it.register() }
    }
}