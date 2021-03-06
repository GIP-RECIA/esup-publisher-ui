'use strict';

angular.module('publisherApp')
    .directive('altDatepicker', function($filter, modernizr) {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModelCtrl) {

                var settings = $.extend (
                    {},
                    $.datepicker.regional['fr'],
                    {
                        dateFormat:  'dd/mm/yy', // My chosen display format
                        altFormat: 'yy-mm-dd',
                        firstDay: 1,
                        changeMonth: false,       // I wanted a month selector
                        onSelect:    onSelect    // I'll use this to update the model
                    }
                );



                // Check for native input[date] availability
                if (!modernizr.hasOwnProperty('inputtypes') || !modernizr.inputtypes.date) {

                    if(attrs.min) {
                        settings.minDate = dateFromISO8601(attrs.min);
                        //console.log("setting mindate ",settings.minDate, dateFromISO8601(attrs.min), attrs.min);
                    }

                    if(attrs.max) {
                        settings.maxDate = dateFromISO8601(attrs.max);
                        //console.log("setting maxdate ",settings.maxDate, dateFromISO8601(attrs.max), attrs.max);
                    }

                    // I wanted a year selector if minDate and maxDate are set
                    if(settings.hasOwnProperty('minDate') && settings.hasOwnProperty('maxDate')) {
                        var minYear = settings.minDate.getFullYear(),
                            maxYear = settings.maxDate.getFullYear();

                        if(minYear !== maxYear) {

                            var range = minYear + ':' + maxYear;

                            settings.changeYear = true;
                            settings.yearRange  = range;
                        }
                    }

                    /*ngModelCtrl.$parsers.push(function(value) {
                        console.log("parser", value);
                        return new Date($filter('date')(value,'yyyy-MM-dd'));
                    });*/

                    ngModelCtrl.$formatters.unshift(function (value) {
                        var match =  value.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
                        if (match) return value;
                        return $filter('date')(value, 'dd/MM/yyyy');

                    });

                    element.datepicker(settings).on('change input', function(evt) {
                        onSelect(evt.target.value);
                    });
                }

                function onSelect(date) {
                    scope.$apply(function () {

                        var parsedDate = jQuery.datepicker.parseDate(settings.dateFormat, date),
                            mysqlDate  = $filter('date')(parsedDate, 'yyyy-MM-dd');

                        ngModelCtrl.$setViewValue(mysqlDate);
                    });
                }

                function dateFromISO8601(isoDateString) {
                    var parts = isoDateString.match(/\d+/g);
                    var isoTime = Date.UTC(parts[0], parts[1] - 1, parts[2], parts[3], parts[4], parts[5]);
                    var isoDate = new Date(isoTime);

                    return isoDate;
                }
            }
        };
    });
