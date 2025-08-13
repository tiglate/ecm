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

function resetForm(caller) {
    const form = __getFormIdFromDataSet(caller);
    if (form != null) {
        form.reset();
    }
}

function submitForm(caller) {
    const form = __getFormIdFromDataSet(caller);
    if (form != null) {
        form.submit();
    }
}

function clearForm() {
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
}

(function () {
    'use strict';

    function initTooltips() {
        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        [...tooltipTriggerList].forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
    }

    document.addEventListener('DOMContentLoaded', initTooltips);
    document.body.addEventListener('htmx:afterSwap', initTooltips);
})();