package greenlight

/**
 * This class will manage study list in cut-up room
 * This class is Singleton (one instance)
 * "it is an arbitrary text on consent status page"
 *  Created by soheil on 25/04/2014.
 */
class Study {

	String description
    static constraints = {
		description nullable: true
    }
    static mapping = {
        description type: "text"
    }
}
