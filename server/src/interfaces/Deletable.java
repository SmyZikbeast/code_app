package interfaces;

import Resources.RepositoryResponse;

public interface Deletable {
    RepositoryResponse deleteTask(int id, String token);
}
