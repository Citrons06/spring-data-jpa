package datajpa.repository;

import datajpa.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemRepositoryTest {

    @Autowired ItemRepository repository;

    @Test
    public void save() {
        Item item = new Item("A");
        repository.save(item);
    }
}
