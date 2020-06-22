package com.xyz.vendingmachine.machine.controller;

import com.xyz.vendingmachine.machine.dto.ReadInventoryItemDTO;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.StorageLocation;
import com.xyz.vendingmachine.machine.service.ItemService;
import com.xyz.vendingmachine.machine.service.StorageLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for the inventory of the machine
 * @author amarenco
 */
@RestController
@RequestMapping("/inventory")
@Validated
public class InventoryController {

    @Autowired
    private StorageLocationService storageLocationService;

    @Autowired
    private ItemService itemService;


    /**
     * @return <code>200</code> with the list of all inventory; or
     * <code>204</code> if the inventory is empty
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAll() {
        List<StorageLocation> locations = storageLocationService.listItems();
        return CollectionUtils.isEmpty(locations) ? ResponseEntity.noContent().build() :
                ResponseEntity.ok(locations.stream().map(ReadInventoryItemDTO::new).collect(Collectors.toList()));
    }


    /**
     * @return <code>200</code> with the item; or
     * <code>404</code> if the item is not found
     */
    @GetMapping(path = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getOne(@PathVariable("code") String code) {
        StorageLocation location = storageLocationService.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No item assigned on the given code"));

        return ResponseEntity.ok(new ReadInventoryItemDTO(location));
    }


    /**
     * Defined the item in the given code
     * @param code the code
     * @param itemId the id of the item
     * @param quantity the quantity of the items
     * @return <code>200</code> if the item was successfully defined; or
     * <code>404</code> if the item was not found; or
     * <code>400</code> if the data is invalid
     */
    @PostMapping(path = "/{code}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> defineItem(
            @PathVariable("code") String code,
            @RequestParam("itemId") @NotBlank(message = "The item must be provided") String itemId,
            @RequestParam("quantity") @Min(message = "The quantity must be greater than 0", value = 0) Integer quantity) {

        Item item = itemService.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        storageLocationService.defineItem(code, item, quantity);

        return ResponseEntity.ok().build();
    }


    /**
     * Clears the location
     * @param code the code
     * @return <code>200</code> if the item was successfully cleared
     */
    @DeleteMapping(path = "/{code}")
    public ResponseEntity<?> clearLocation(@PathVariable("code") String code) {
        storageLocationService.clearLocation(code);
        return ResponseEntity.ok().build();
    }
}
