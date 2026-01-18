public abstract class AbstractStoredProcedureCaller {
    private static final Logger log = LoggerFactory.getLogger(AbstractStoredProcedureCaller.class);
    protected final EntityManager em;

    protected AbstractStoredProcedureCaller(EntityManager em) {
        this.em = em;
    }

    protected Map<String, Object> executeProcedure(
            String procName,
            StoredProcedureParam[] paramDefs,
            ParamValue... paramVals) {

        try {
            // 1. Determine Result Class using Streams
            Class<?> resultClass = Arrays.stream(paramDefs)
                    .filter(p -> p.mode() == ParameterMode.REF_CURSOR && p.type() != Void.class)
                    .map(StoredProcedureParam::type)
                    .findFirst()
                    .orElse(null);

            StoredProcedureQuery query = (resultClass != null) 
                    ? em.createStoredProcedureQuery(procName, resultClass) 
                    : em.createStoredProcedureQuery(procName);

            // 2. Register Parameters
            Arrays.stream(paramDefs).forEach(p -> 
                query.registerStoredProcedureParameter(p.name(), p.type(), p.mode()));

            // 3. Set Input Values
            if (paramVals != null) {
                Arrays.stream(paramVals).forEach(pv -> 
                    query.setParameter(pv.param().name(), pv.value()));
            }

            query.execute();

            // 4. Collect Outputs using a Stream/Collector
            return Arrays.stream(paramDefs)
                    .filter(p -> p.mode() != ParameterMode.IN)
                    .collect(LinkedHashMap::new, 
                             (map, p) -> map.put(p.name(), extractValue(query, p)), 
                             Map::putAll);

        } catch (PersistenceException ex) {
            log.error("Error executing stored procedure {}", procName, ex);
            throw new DatabaseException("Failed to execute stored procedure: " + procName, ex);
        }
    }

    private Object extractValue(StoredProcedureQuery query, StoredProcedureParam p) {
        if (p.mode() == ParameterMode.REF_CURSOR) {
            try {
                return query.getResultList();
            } catch (IllegalStateException e) {
                return query.getOutputParameterValue(p.name());
            }
        }
        return query.getOutputParameterValue(p.name());
    }
}
