import 'bootstrap';
import htmx from 'htmx.org';
import flatpickr from 'flatpickr';
import 'scss/app.scss';

/**
 * Register an event at the document for the specified selector,
 * so events are still catch after DOM changes.
 */
function handleEvent(eventType, selector, handler) {
    document.addEventListener(eventType, function (event) {
        if (event.target.matches(selector + ', ' + selector + ' *')) {
            handler.apply(event.target.closest(selector), arguments);
        }
    });
}

handleEvent('change', '.js-selectlinks', function () {
    htmx.ajax('get', this.value, document.body);
    history.pushState({htmx: true}, '', this.value);
});

handleEvent('click', '.js-file-delete', function (event) {
    const $fileDiv = event.target.parentElement;
    const $fileRow = $fileDiv.previousElementSibling;
    $fileRow.removeAttribute('disabled');
    $fileRow.classList.remove('d-none');
    $fileDiv.remove();
});

function __getFormIdFromDataSet(caller) {
    const formId = caller.dataset.formId;
    if (!formId) {
        console.error("Error: Form ID not found in data-form-id attribute.");
        return null;
    }
    const form = document.getElementById(formId);
    if (!form || form.tagName.toLowerCase() !== 'form') {
        console.error("Error: Element with ID '" + formId + "' is not a form or does not exist.");
        return null;
    }
    return form;
}

window.resetForm = function(caller) {
    const form = __getFormIdFromDataSet(caller);
    if (form != null) {
        form.reset();
    }
};

window.submitForm = function(caller) {
    const form = __getFormIdFromDataSet(caller);
    if (form != null) {
        form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    }
};

window.clearForm = function() {
    const form = document.getElementById('searchForm');
    const inputElements = form.querySelectorAll('input, select, textarea');

    inputElements.forEach(input => {
        if (input.type === 'checkbox' || input.type === 'radio') {
            input.checked = false;
        } else if (input.tagName.toLowerCase() === 'select') {
            // Deselect all options first
            for (let option of input.options) {
                option.selected = false;
            }
            const emptyOption = input.querySelector('option[value=""]');
            if (emptyOption) {
                emptyOption.selected = true;
            } else {
                input.selectedIndex = -1;
            }
        } else if (input.type !== 'button' && input.type !== 'submit' && input.type !== 'reset') {
            input.value = '';
        }
    });
    return false;
};

(function () {
    'use strict';

    function initDatepicker() {
        document.querySelectorAll('.js-datepicker, .js-timepicker, .js-datetimepicker').forEach(($item) => {
            const flatpickrConfig = {
                allowInput: true,
                time_24hr: true,
                enableSeconds: true
            };
            if ($item.classList.contains('js-datepicker')) {
                flatpickrConfig.dateFormat = 'Y-m-d';
            } else if ($item.classList.contains('js-timepicker')) {
                flatpickrConfig.enableTime = true;
                flatpickrConfig.noCalendar = true;
                flatpickrConfig.dateFormat = 'H:i:S';
            } else { // datetimepicker
                flatpickrConfig.enableTime = true;
                flatpickrConfig.altInput = true;
                flatpickrConfig.altFormat = 'Y-m-d H:i:S';
                flatpickrConfig.dateFormat = 'Y-m-dTH:i:S';
                // workaround label issue
                flatpickrConfig.onReady = function () {
                    const id = this.input.id;
                    this.input.id = null;
                    this.altInput.id = id;
                };
            }
            flatpickr($item, flatpickrConfig);
        });
    }

    document.addEventListener('htmx:afterSwap', initDatepicker);
    initDatepicker();

    function initTooltips() {
        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        [...tooltipTriggerList].forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
    }

    document.addEventListener('DOMContentLoaded', initTooltips);
    document.body.addEventListener('htmx:afterSwap', initTooltips);
})();