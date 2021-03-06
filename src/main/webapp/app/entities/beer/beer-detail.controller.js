(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .controller('BeerDetailController', BeerDetailController);

    BeerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Beer', 'Tasting'];

    function BeerDetailController($scope, $rootScope, $stateParams, entity, Beer, Tasting) {
        var vm = this;
        vm.beer = entity;
        
        var unsubscribe = $rootScope.$on('brewtasteApp:beerUpdate', function(event, result) {
            vm.beer = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
