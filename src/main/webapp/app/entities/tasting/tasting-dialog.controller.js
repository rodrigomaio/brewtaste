(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .controller('TastingDialogController', TastingDialogController);

    TastingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Tasting', 'Beer', 'User'];

    function TastingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Tasting, Beer, User) {
        var vm = this;
        vm.tasting = entity;
        vm.beers = Beer.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

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
