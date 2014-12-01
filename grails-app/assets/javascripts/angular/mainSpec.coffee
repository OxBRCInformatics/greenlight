#= require  main

describe "mytest suite", ()->

  controller = null
  scope = null

  beforeEach module("greenlight")

  beforeEach inject ($controller,$rootScope) ->
    scope = $rootScope.$new()
    controller = $controller "ExampleController" , {$scope:scope}

  it "init will create valid birthYear for patient dateOfBirth.year", ()->
    expect(true).toEqual(true)

    scope.consent =
      patient :
        firstName :"pName"
        lastName :"pLastName"
        dateOfBirth:
          year:2000
          month:'1'
          date:'1'
    currentYear = new Date().getUTCFullYear()
    scope.init()

    yearsRange = 100

    expect(scope?.currentYear).toEqual(currentYear)
    expect(scope?.validYears[0]).toEqual(scope.consent.patient.dateOfBirth.year - yearsRange)
    expect(scope?.validYears[scope?.validYears?.length - 1]).toEqual(currentYear)


  it "init will create valid birthYear for new consent Form", ()->
    currentYear = new Date().getUTCFullYear()

    scope.consent = null
    scope.init()
    yearsRange = 100

    expect(scope?.currentYear).toEqual(currentYear)
    expect(scope?.validYears?.length).toEqual(yearsRange + 1)
    expect(scope?.validYears[yearsRange]).toEqual(currentYear)
    expect(scope?.validYears[0]).toEqual(currentYear - yearsRange)


  it "init will create valid consent year range", ()->
    currentYear = new Date().getUTCFullYear()

    scope.consent = null
    scope.init()
    yearsRange = 100

    expect(scope?.currentYear).toEqual(currentYear)
    expect(scope?.consentValidYears?.length).toEqual(yearsRange + 1)
    expect(scope?.consentValidYears[yearsRange]).toEqual(currentYear)
    expect(scope?.consentValidYears[0]).toEqual(currentYear - yearsRange)


  it "ConsentFormTemplate change will update the consent repsonse list with question default values" , () ->
    #An empty new consent
    #When the controller loads for the first time, it calls the watch
    scope.$apply()
    expect(scope.consent).not.toBeNull()

    #user selects a new consentFormTemplate
    tempForm =
      id:"1"
      name:"GEL Form"
      prefix:"GEL"
      questions:[
        {id:"1",defaultValue:"2",title:"Q-GEL1",validValues :[{name:"YES",value:"1"},{name:"NO",value:"0"},{name:"BLANK",value:"2"}]},
        {id:"2",defaultValue:"2",title:"Q-GEL2",validValues :[{name:"YES",value:"1"},{name:"NO",value:"0"},{name:"BLANK",value:"2"}]},
        {id:"3",defaultValue:"2",title:"Q-GEL3",validValues :[{name:"YES",value:"1"},{name:"NO",value:"0"},{name:"BLANK",value:"2"}]},
        {id:"4",defaultValue:"2",title:"Q-GEL4",validValues :[{name:"YES",value:"1"},{name:"NO",value:"0"},{name:"BLANK",value:"2"}]}]

    #directive will set the new selected consentForm to user consent
    scope.consent = {}
    scope.consent.consentFormTemplate = tempForm
    scope.$apply()

    expect(scope.consent.responses).not.toBeNull()
    expect(scope.consent.responses.length).toEqual(4)
#    expect(scope.consent.responses[0].answer).toEqual(scope.consent.responses[0].question.defaultValue)
#
#
#    #if user selects a different consentTemplate, it should load all the questions
#    tempForm =
#      id:"2"
#      name:"ORB Form"
#      prefix:"ORB"
#      questions:[
#        {id:"1",defaultValue:"2",title:"Q-GEL1",validValues :[{name:"YES",value:"1"},{name:"NO",value:"0"},{name:"BLANK",value:"2"}]},
#        {id:"2",defaultValue:"2",title:"Q-GEL2",validValues :[{name:"YES",value:"1"},{name:"NO",value:"0"},{name:"BLANK",value:"2"}]}]
#
#    #directive will set the new selected consentForm to user consent
#    scope.consent = {}
#    scope.consent.consentFormTemplate = tempForm
#    scope.$apply()
#
#    expect(scope.consent.responses).not.toBeNull()
#    expect(scope.consent.responses.length).toEqual(2)
#    expect(scope.consent.responses[0].answer).toEqual(scope.consent.responses[0].question.defaultValue)