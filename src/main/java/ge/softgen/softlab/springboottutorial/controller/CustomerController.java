package ge.softgen.softlab.springboottutorial.controller;

import ge.softgen.softlab.springboottutorial.exception.InvalidParameterException;
import ge.softgen.softlab.springboottutorial.exception.NotFoundException;
import ge.softgen.softlab.springboottutorial.entity.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerController {
    private static int id = 0;

    private List<Customer> db = new ArrayList<>();

    @GetMapping("/customers")
    public List<Customer> getAll() {

        return db.stream().filter(c -> !c.getDelete()).toList();
    }

    @GetMapping("/customers/{id}")
    public Customer getById (@PathVariable int id) {
        if (id<1){
            throw new InvalidParameterException("Id must be positive integer");
        }
            return getCustomer(id);
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> add(@RequestBody Customer customer) {
        customer.setId(++id);
        customer.setDelete(false);
        db.add(customer);
        var location = UriComponentsBuilder.fromPath("./customers/" + id).build().toUri();
        return ResponseEntity.created(location).body(customer);
    }

    @PutMapping("/customers/{id}")
    public Customer update (@RequestBody Customer customer, @PathVariable int id) {
            var foundCustomer = getCustomer(id);
            foundCustomer.setFirstName(customer.getFirstName());
            foundCustomer.setLastName(customer.getLastName());
            foundCustomer.setBirthDate(customer.getBirthDate());
            return foundCustomer;
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Customer> delete(@PathVariable int id) {
            var foundCustomer = getCustomer(id);
            foundCustomer.setDelete(true);
            return ResponseEntity.noContent().build();
        }

    private Customer getCustomer(int id) {
        var optional = db.stream().filter(c -> c.getId() == id).findFirst();
        if (optional.isEmpty()) {
            throw new NotFoundException("Customer not found");
        }
        return optional.get();
    }
}
