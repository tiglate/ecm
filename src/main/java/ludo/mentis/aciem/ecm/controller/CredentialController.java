package ludo.mentis.aciem.ecm.controller;


import ludo.mentis.aciem.commons.web.CustomCollectors;
import ludo.mentis.aciem.commons.web.FlashMessages;
import ludo.mentis.aciem.commons.web.PaginationUtils;
import ludo.mentis.aciem.commons.web.SortUtils;
import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.model.*;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.service.CredentialService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static java.util.Map.entry;

@Controller
@RequestMapping("/credentials")
public class CredentialController {

    private static final String ENTITY_NAME = "Credential";
    private static final String CONTROLLER_ADD = "credential/add";
    private static final String CONTROLLER_EDIT = "credential/edit";
    private static final String CONTROLLER_VIEW = "credential/view";
    private static final String CONTROLLER_LIST = "credential/list";
    private static final String CONTROLLER_HISTORY = "credential/history";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/credentials";

    private final SortUtils sortUtils;
    private final PaginationUtils paginationUtils;
    private final CredentialService credentialService;
    private final BusinessAppRepository applicationRepository;

    public CredentialController(final SortUtils sortUtils,
                                final PaginationUtils paginationUtils,
                                final CredentialService credentialService,
                                final BusinessAppRepository applicationRepository) {
        this.sortUtils = sortUtils;
        this.paginationUtils = paginationUtils;
        this.credentialService = credentialService;
        this.applicationRepository = applicationRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("environmentValues", Environment.values());
        model.addAttribute("credentialTypeValues", CredentialType.values());
        model.addAttribute("applicationValues", applicationRepository.findAll(Sort.by("name"))
                .stream()
                .collect(CustomCollectors.toLinkedHashMap(BusinessApp::getId, BusinessApp::getName)));
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.DEVELOPER + "')")
    @GetMapping
    public String list(@ModelAttribute("credentialSearch") CredentialDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
                entry("id", "sortById"),
                entry("application", "sortByApplication"),
                entry("username", "sortByUsername"),
                entry("version", "sortByVersion"),
                entry("createdAt", "sortByCreatedAt"),
                entry("createdBy", "sortByCreatedBy")
        ));
        if (filter.getEnabled() == null) {
            filter.setEnabled(true);
        }
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var credentials = credentialService.findAll(filter, pageRequest);
        model.addAttribute("credentials", credentials);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", paginationUtils.getPaginationModel(credentials));
        return CONTROLLER_LIST;
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.DEVELOPER + "')")
    @GetMapping("/view/{id}")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("credential", credentialService.get(id));
        return CONTROLLER_VIEW;
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.DEVELOPER + "')")
    @GetMapping("/history/{id}")
    public String history(@PathVariable final Long id, final Model model) {
        var credentials = credentialService.findHistory(id);
        model.addAttribute("credentials", credentials);
        return CONTROLLER_HISTORY;
    }

    @PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model, final RedirectAttributes redirectAttributes) {
        var credential = credentialService.get(id);
        if (!credential.getIsLatest()) {
            FlashMessages.error(redirectAttributes, "You cannot update an old version!");
            return REDIRECT_TO_CONTROLLER_INDEX;
        }
        model.addAttribute("credential", credential);
        return CONTROLLER_EDIT;
    }

    @PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
    @GetMapping("/add")
    public String add(@ModelAttribute("credential") final CredentialDTO credentialDTO) {
        return CONTROLLER_ADD;
    }

    @PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("credential") @Validated(OnCreate.class) final CredentialDTO credentialDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        credentialService.create(credentialDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("credential") @Validated(OnUpdate.class) final CredentialDTO credentialDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        credentialService.update(id, credentialDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        credentialService.delete(id);
        FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }
}
