package com.xyz.vendingmachine.machine.controller;

import com.xyz.vendingmachine.machine.dto.SaveItemDTO;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.service.ItemService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * REST Controller for {@link Item} entities
 * @author amarenco
 */
@RestController
@RequestMapping("/item")
@Validated
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * @return <code>200</code> with a list of all items in the machine; or
     * <code>204</code> if no items are found
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAll() {
        List<Item> items = itemService.getAll();
        return CollectionUtils.isEmpty(items) ? ResponseEntity.noContent().build() : ResponseEntity.ok(items);
    }


    /**
     * @param id the id of the item to find
     * @return <code>200/code> with the details of the item; od
     * <code>404</code> if the item was not found
     */
    @GetMapping(path ="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(itemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found")));
    }


    /**
     * Creates a new item
     * @param item the data for the new item
     * @param uriBuilder a builder for URI components
     * @return <code>201</code> with the id of the new item
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(
            @RequestBody @Valid SaveItemDTO item,
            UriComponentsBuilder uriBuilder) {

        Item createdItem = itemService.save(item.toItem());

        return ResponseEntity
                .created(uriBuilder.path("/item/{id}").buildAndExpand(createdItem.getId()).toUri())
                .body(Collections.singletonMap("id", createdItem.getId()));
    }


    /**
     * Updates an item
     * @param id the id of the item to update
     * @param item the new data for the item
     * @return <code>200</code> if the item was updated; or
     * <code>404</code> if the item was not found
     */
    @PutMapping(path ="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(
            @PathVariable("id") String id,
            @RequestBody @Valid SaveItemDTO item) {

        Item currentItem = itemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        itemService.save(item.toItem(currentItem));

        return ResponseEntity.ok().build();
    }


    /**
     * Deletes an item
     * @param id the id of the item to update
     * @return <code>200</code> if the item was deleted; or
     * <code>404</code> if the item was not found
     */
    @DeleteMapping(path ="/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        Item item = itemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        itemService.delete(item);

        return ResponseEntity.ok().build();
    }
}
