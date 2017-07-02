package com.example.nouno.locateme.Djikstra;

import java.util.List;

/**
 * Created by nouno on 25/06/2017.
 */
public class Vertex {
    final private Long id;



    public Vertex(long id) {
        this.id = id;

    }
    public long getId() {
        return id;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    public static Vertex getVertexById (List<Vertex> vertices, long id)
    {
        for (Vertex v:vertices)
        {
            if (v.getId()==id)
            {
                return v;
            }
        }
        return null;
    }



}
