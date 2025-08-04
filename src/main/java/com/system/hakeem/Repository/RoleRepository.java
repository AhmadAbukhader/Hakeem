package com.system.hakeem.Repository;

import com.system.hakeem.Model.Role;
import com.system.hakeem.Model.Type;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
 Role findById(int id );
 Role findByRole(Type role);
}
