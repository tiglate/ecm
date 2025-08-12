package ludo.mentis.aciem.ecm.controller;


import jakarta.validation.Valid;
import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.service.CredentialService;
import ludo.mentis.aciem.ecm.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/credentials";
    private final CredentialService credentialService;
    private final BusinessAppRepository applicationRepository;
    private final SortUtils sortUtils;

    public CredentialController(final CredentialService credentialService,
                                final BusinessAppRepository applicationRepository) {
        this.credentialService = credentialService;
        this.applicationRepository = applicationRepository;
        this.sortUtils = new SortUtils();
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("environmentValues", Environment.values());
        model.addAttribute("credentialTypeValues", CredentialType.values());
        model.addAttribute("applicationValues", applicationRepository.findAll(Sort.by("name"))
                .stream()
                .collect(CustomCollectors.toSortedMap(BusinessApp::getId, BusinessApp::getName)));
    }

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
                entry("enabled", "sortByEnabled"),
                entry("version", "sortByVersion"),
                entry("createdAt", "sortByCreatedAt"),
                entry("createdBy", "sortByCreatedBy")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var credentials = credentialService.findAll(filter, pageRequest);
        model.addAttribute("credentials", credentials);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", PaginationUtils.getPaginationModel(credentials));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("credential", credentialService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("credential", credentialService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("credential") final CredentialDTO credentialDTO) {
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("credential") @Valid final CredentialDTO credentialDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        credentialService.create(credentialDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("credential") @Valid final CredentialDTO credentialDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        credentialService.update(id, credentialDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        credentialService.delete(id);
        FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }
}
