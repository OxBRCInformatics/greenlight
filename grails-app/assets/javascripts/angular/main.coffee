#= require angular/angular.js
#= require directives.js
#= require services.js

app = angular.module('greenlight',['consentQuestionTemplate','ui.bootstrap',"greenlight.services"]).config( (datepickerConfig) ->
  datepickerConfig.showWeeks = false;
  )

app.controller('ExampleController', ['$scope','$filter','greenlight.services.attachment', ($scope,$filter,attachment) ->
#  By default it is empty
  $scope.consent =
    responses = []

#    just a simple test
#  $scope.data = attachment.get(1)
  $scope.data = attachment.list()


  $scope.nhsNumberPattern = /^\d{10}$/
#  $scope.formIdPattern = new RegExp("^\\d{5}$")
#  TAKE CARE, if it is a string you need to escape backslashes
#  Backslashes are special characters in strings that need to be escaped with another backslash:
#  /^\d{5}$/ OR "^\\d{5}$"


#  dt should be formatted as yyy-MM-dd for setting datepicker
  $scope.dt = $filter('date')(new Date(), 'yyyy-MM-dd')

  $scope.today = ->
    $scope.dt = new Date()
    return

  $scope.clear = ->
   $scope.dt = null
   return

  $scope.today  =  $filter('date')(new Date(), 'yyyy-MM-dd')

  $scope.open = ($event) ->
   $event.preventDefault()
   $event.stopPropagation()
   $scope.opened = true
   return

  $scope.dateOptions =
   formatYear: "yy"
   startingDay: 1
   showWeeks:false


  $scope.format = "dd.MM.yyyy"




  validValues0 = [{name:"YES",value:"5"},{name:"NO",value:"6"},{name:"BLANK",value:"8"}]
  validValues1 = [{name:"YES",value:"5"},{name:"NO",value:"6"},{name:"BLANK",value:"2"}]

  $scope.allForms = [
    {id:"1",name:"GEL Form",prefix:"GEL",formIdPattern:"^\\d{5}$",questions:[
      {id:"1",defaultValue:"8",title:"Q-GEL1",validValues :validValues0},
      {id:"2",defaultValue:"8",title:"Q-GEL2",validValues :validValues0},
      {id:"3",defaultValue:"8",title:"Q-GEL3",validValues :validValues0},
      {id:"4",defaultValue:"8",title:"Q-GEL4",validValues :validValues0}]},

    {id:"2",name:"ORB Form",prefix:"ORB",formIdPattern:"^\\d{5}$",questions:[
      {id:"5",defaultValue:validValues1[2],title:"Q-ORB1",validValues :validValues1},
      {id:"6",defaultValue:validValues1[2],title:"Q-ORB2",validValues :validValues1}]}

  ]

  buildDefaultData = () ->
    $scope.consent = []
    $scope.consent.patient =
      firstName: "AA"
      lastName: "BB"
      dateOfBirth:
        year: "2010"

    $scope.consent.consentFormTemplate = $scope.allForms[0]

    $scope.consent.responses = [
      {id:"1",answer:validValues0[0],question:$scope.allForms[0].questions[0]},
      {id:"2",answer:validValues0[1],question:$scope.allForms[0].questions[1]},
      {id:"3",answer:validValues0[1],question:$scope.allForms[0].questions[2]},
      {id:"4",answer:validValues0[0],question:$scope.allForms[0].questions[3]}
    ]

  $scope.init = () ->

    #buildDefaultData()

    $scope.validYears = []
    $scope.validMonths = []
    $scope.validDays = []
    $scope.consentValidYears = []

    $scope.currentYear = new Date().getUTCFullYear()
    startYear = $scope.currentYear - 100

    if($scope?.consent?.patient?.dateOfBirth?.year)
      startYear = $scope.consent.patient.dateOfBirth.year - 100

    for year in [startYear...$scope.currentYear+1]
      $scope.validYears.push(year)

    #Consent Valid Years
    for year in [$scope.currentYear - 100...$scope.currentYear + 1]
      $scope.consentValidYears.push(year)


  $scope.$watch 'consent.consentFormTemplate', (newValue,oldValue) ->

    #A new consent Form is going to be added
    if(!newValue)
      $scope?.consent?.responses = []
      return

    #A consentForm is going to be edited
    if(newValue && newValue == oldValue)
      return

    # if it is a new consent and it's null, create it
    if(!$scope.consent)
      $scope.consent = {}
    $scope.consent.responses = []

    for question in newValue.questions
      $scope.consent.responses.push({id:"0",question:question})
])


app.directive('overwriteEmail', ->
  require: 'ngModel',
  restrict: '',
  link: (scope, elm, attrs, ctrl) ->
    if (ctrl && ctrl.$validators.required)
      ctrl.$validators.required = (modelValue) ->
        debugger
        return false
    return
)


#app.directive "consentFormId", ($q, $timeout) ->
#  scope:
#  consentFormTemplate : '='
#  require: "ngModel"
#  link: (scope, elm, attrs, ctrl) ->
#    ctrl.$asyncValidators.myValidator = (modelValue, viewValue) ->
#
#      return $q.when()  if ctrl.$isEmpty(modelValue)
#
#      def = $q.defer()
#
#      $timeout (->
#        unless 1 is 1
#          scope.$parent.foundFormId = "LINK"
#          def.resolve()
#        else
#          def.reject()
#        return
#      ), 500
#
#      def.promise
#
#    return