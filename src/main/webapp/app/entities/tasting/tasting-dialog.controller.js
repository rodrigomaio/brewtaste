(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .controller('TastingDialogController', TastingDialogController);

    TastingDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Tasting', 'Beer'];

    function TastingDialogController ($scope, $stateParams, $uibModalInstance, entity, Tasting, Beer) {
        var vm = this;
        vm.tasting = entity;
        vm.beers = Beer.query();
        vm.load = function(id) {
            Tasting.get({id : id}, function(result) {
                vm.tasting = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('brewtasteApp:tastingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.tasting.id !== null) {
                Tasting.update(vm.tasting, onSaveSuccess, onSaveError);
            } else {
                Tasting.save(vm.tasting, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.date = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
