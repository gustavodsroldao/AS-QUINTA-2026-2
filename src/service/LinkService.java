package service;

import adapters.DatabaseStorage;
import domain.Link;

public class LinkService extends BaseService {
    public LinkService() {
        this.armazenamento = new DatabaseStorage<>(Link.class);
    }
}
