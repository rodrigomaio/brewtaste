'use strict';

describe('Controller Tests', function() {

    describe('Tasting Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTasting, MockBeer;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTasting = jasmine.createSpy('MockTasting');
            MockBeer = jasmine.createSpy('MockBeer');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Tasting': MockTasting,
                'Beer': MockBeer
            };
            createController = function() {
                $injector.get('$controller')("TastingDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'brewtasteApp:tastingUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
