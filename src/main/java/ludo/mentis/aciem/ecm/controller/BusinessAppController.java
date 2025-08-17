package ludo.mentis.aciem.ecm.controller;


import jakarta.validation.Valid;
import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import ludo.mentis.aciem.ecm.service.BusinessAppService;
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
@RequestMapping("/applications")
public class BusinessAppController {

    private static final String ENTITY_NAME = "Application";
    private static final String CONTROLLER_ADD = "businessApp/add";
    private static final String CONTROLLER_EDIT = "businessApp/edit";
    private static final String CONTROLLER_VIEW = "businessApp/view";
    private static final String CONTROLLER_LIST = "businessApp/list";
    private static final String REDIRECT_TO_CONTROLLER_INDEX = "redirect:/applications";
    private final BusinessAppService businessAppService;
    private final SortUtils sortUtils;

    public BusinessAppController(final BusinessAppService businessAppService) {
        this.businessAppService = businessAppService;
        this.sortUtils = new SortUtils();
    }

    @GetMapping
    public String list(@ModelAttribute("businessAppSearch") BusinessAppDTO filter,
                       @RequestParam(required = false) String sort,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        if (sort == null) {
            sort = "id,desc";
        }
        final var sortOrder = this.sortUtils.addSortAttributesToModel(model, sort, pageable, Map.ofEntries(
                entry("id", "sortById"),
                entry("code", "sortByCode"),
                entry("name", "sortByName")
        ));
        final var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        final var businessApps = businessAppService.findAll(filter, pageRequest);
        model.addAttribute("businessApps", businessApps);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", PaginationUtils.getPaginationModel(businessApps));
        return CONTROLLER_LIST;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable final Long id, final Model model) {
        model.addAttribute("businessApp", businessAppService.get(id));
        return CONTROLLER_VIEW;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("businessApp", businessAppService.get(id));
        return CONTROLLER_EDIT;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("businessApp") final BusinessAppDTO businessAppDTO) {
        return CONTROLLER_ADD;
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("businessApp") @Valid final BusinessAppDTO businessAppDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_ADD;
        }
        businessAppService.create(businessAppDTO);
        FlashMessages.createSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
                       @ModelAttribute("businessApp") @Valid final BusinessAppDTO businessAppDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return CONTROLLER_EDIT;
        }
        businessAppService.update(id, businessAppDTO);
        FlashMessages.updateSuccess(redirectAttributes, ENTITY_NAME);
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = businessAppService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(FlashMessages.MSG_ERROR,
                    GlobalizationUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            businessAppService.delete(id);
            FlashMessages.deleteSuccess(redirectAttributes, ENTITY_NAME);
        }
        return REDIRECT_TO_CONTROLLER_INDEX;
    }

}
