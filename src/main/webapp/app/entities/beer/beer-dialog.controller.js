(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .controller('BeerDialogController', BeerDialogController);

    BeerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Beer', 'Tasting'];

    function BeerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Beer, Tasting) {
        var vm = this;
        vm.beer = entity;
        vm.tastings = Tasting.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('brewtasteApp:beerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.beer.id !== null) {
                Beer.update(vm.beer, onSaveSuccess, onSaveError);
            } else {
                Beer.save(vm.beer, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
