package ludo.mentis.aciem.ecm.controller;


import jakarta.validation.Valid;
import ludo.mentis.aciem.commons.web.CustomCollectors;
import ludo.mentis.aciem.commons.web.FlashMessages;
import ludo.mentis.aciem.commons.web.PaginationUtils;
import ludo.mentis.aciem.commons.web.SortUtils;
import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.service.ApiKeyService;
import ludo.mentis.aciem.ecm.util.UserRoles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static java.util.Map.entry;

@Controller
@RequestMapping("/apiKeys")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class ApiKeyController {

    private static final String ENTITY_NAME = "ApiKey";
    private static final String CONTROLLER_ADD = "apiKey/add";
    private static final String CONTROLLER_EDIT = "apiKey/edit";
    private static final String CONTROLLER_VIEW = "apiKey/view";
    private static final String CONTROLLER_LIST = "apiKey/list";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/apiKeys";

    private final SortUtils sortUtils;
    private final ApiKeyService apiKeyService;
    private final PaginationUtils paginationUtils;
    private final BusinessAppRepository applicationRepository;

    public ApiKeyController(final SortUtils sortUtils,
                            final ApiKeyService apiKeyService,
                            final PaginationUtils paginationUtils,
                            final BusinessAppRepository applicationRepository) {
        this.sortUtils = sortUtils;
        this.apiKeyService = apiKeyService;
        this.paginationUtils = paginationUtils;
        this.applicationRepository = applicationRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("environmentValues", Environment.values());
        model.addAttribute("applicationValues", applicationRepository.findAll(Sort.by("name"))
                .stream()
                .collect(CustomCollectors.toLinkedHashMap(BusinessApp::getId, BusinessApp::getName)));
    }

    @GetMapping
    public String list(@ModelAttribute("apiKeySearch") ApiKeyDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
                entry("id", "sortById"),
                entry("application", "sortByApplication"),
                entry("clientId", "sortByClientId"),
                entry("server", "sortByServer"),
                entry("createdAt", "sortByCreatedAt"),
                entry("UpdatedAt", "sortByUpdatedAt"),
                entry("UpdatedBy", "sortByUpdatedBy")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var apiKeys = apiKeyService.findAll(filter, pageRequest);
        model.addAttribute("apiKeys", apiKeys);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", paginationUtils.getPaginationModel(apiKeys));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("apiKey", apiKeyService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("apiKey", apiKeyService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("apiKey") final ApiKeyDTO apiKeyDTO) {
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("apiKey") @Valid final ApiKeyDTO apiKeyDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        apiKeyService.create(apiKeyDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("apiKey") @Valid final ApiKeyDTO apiKeyDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        apiKeyService.update(id, apiKeyDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        apiKeyService.delete(id);
        FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }
}
