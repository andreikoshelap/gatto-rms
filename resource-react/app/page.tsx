const services = [
  { name: 'Resource API', port: 8085, state: 'Ready', tone: 'green' },
  { name: 'Publisher', port: 8086, state: 'Kafka linked', tone: 'blue' },
  { name: 'Consumer', port: 8087, state: 'Listening', tone: 'green' },
  { name: 'Kafka UI', port: 8080, state: 'Inspect', tone: 'amber' },
];

const resources = [
  {
    id: 'EE-001',
    type: 'Metering point',
    location: 'Tallinn',
    status: 'Active',
    events: 18,
  },
  {
    id: 'LV-014',
    type: 'Connection point',
    location: 'Riga',
    status: 'Syncing',
    events: 7,
  },
  {
    id: 'EE-122',
    type: 'Charging point',
    location: 'Narva',
    status: 'Review',
    events: 3,
  },
];

export default function Home() {
  return (
    <main className="shell">
      <section className="toolbar" aria-label="Application header">
        <div className="brand">
          <img src="/gatto-mark.svg" alt="" width="40" height="40" />
          <div>
            <p className="eyebrow">Gatto RMS</p>
            <h1>Resource operations</h1>
          </div>
        </div>
        <div className="toolbarActions">
          <button type="button" className="iconButton" aria-label="Refresh resources">
            ↻
          </button>
          <button type="button" className="primaryButton">
            New resource
          </button>
        </div>
      </section>

      <section className="summaryGrid" aria-label="Service status">
        {services.map((service) => (
          <article className="statusCard" key={service.name}>
            <div className={`statusDot ${service.tone}`} />
            <div>
              <h2>{service.name}</h2>
              <p>{service.state}</p>
            </div>
            <span className="port">:{service.port}</span>
          </article>
        ))}
      </section>

      <section className="workspace">
        <div className="panel">
          <div className="panelHeader">
            <h2>Resources</h2>
            <span>{resources.length} visible</span>
          </div>
          <div className="resourceList">
            {resources.map((resource) => (
              <article className="resourceRow" key={resource.id}>
                <div>
                  <strong>{resource.id}</strong>
                  <span>{resource.type}</span>
                </div>
                <div>
                  <strong>{resource.location}</strong>
                  <span>{resource.events} events</span>
                </div>
                <span className="pill">{resource.status}</span>
              </article>
            ))}
          </div>
        </div>

        <aside className="activity">
          <h2>Pipeline</h2>
          <ol>
            <li>
              <span>API accepted update</span>
              <time>09:42</time>
            </li>
            <li>
              <span>Publisher emitted event</span>
              <time>09:43</time>
            </li>
            <li>
              <span>Consumer updated read model</span>
              <time>09:43</time>
            </li>
          </ol>
        </aside>
      </section>
    </main>
  );
}
