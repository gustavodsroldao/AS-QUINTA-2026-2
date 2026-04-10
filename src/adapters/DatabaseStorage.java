package adapters;

import domain.EntityInterface;

import java.util.ArrayList;
import java.util.UUID;

public class DatabaseStorage implements PersistInterface {
    private ArrayList<EntityInterface> db = new ArrayList<>();

    @Override
    public void save(EntityInterface entity) {
        db.add(entity);
    }

    @Override
    public void delete(EntityInterface entity) {
        db.remove(entity);
    }

    @Override
    public ArrayList<EntityInterface> listAll() {
        return db;
    }

    @Override
    public EntityInterface findOneById(UUID id) {

        // Percorre DB
        for (int i = 0; i < db.size() ; i++) {

            // Verifica se o UUID do elemento é igual ao UUID passado por parâmetro
            if (db.get(i).getUUID().equals(id)) {
                return db.get(i);
            }
        }

        return null;
    }
}
