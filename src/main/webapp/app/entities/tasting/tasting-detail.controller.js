(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .controller('TastingDetailController', TastingDetailController);

    TastingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Tasting', 'Beer'];

    function TastingDetailController($scope, $rootScope, $stateParams, entity, Tasting, Beer) {
        var vm = this;
        vm.tasting = entity;
        
        var unsubscribe = $rootScope.$on('brewtasteApp:tastingUpdate', function(event, result) {
            vm.tasting = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
