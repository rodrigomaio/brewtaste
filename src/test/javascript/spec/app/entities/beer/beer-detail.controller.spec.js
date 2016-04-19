'use strict';

describe('Controller Tests', function() {

    describe('Beer Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockBeer, MockTasting;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockBeer = jasmine.createSpy('MockBeer');
            MockTasting = jasmine.createSpy('MockTasting');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Beer': MockBeer,
                'Tasting': MockTasting
            };
            createController = function() {
                $injector.get('$controller')("BeerDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'brewtasteApp:beerUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
