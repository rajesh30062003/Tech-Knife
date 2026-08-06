import React, { useState } from 'react';
import { 
  Bot, Sparkles, Send, FileText, Video, AlertTriangle, TrendingUp, DollarSign, 
  Clock, Zap, CheckCircle2, RefreshCw, Layers, ShieldCheck, Play, Plus, ArrowRight,
  Search, Lightbulb, ChevronRight, Sliders, Cpu, UserCheck, Bell
} from 'lucide-react';
import { EnterpriseProject } from '../../../api/projects';

interface EnterpriseAiWorkspaceProps {
  project: EnterpriseProject;
}

interface ChatMessage {
  id: string;
  sender: 'user' | 'ai';
  text: string;
  timestamp: string;
  ragSources?: string[];
}

interface AutomationRule {
  id: string;
  name: string;
  trigger: string;
  action: string;
  enabled: boolean;
}

export const EnterpriseAiWorkspace: React.FC<EnterpriseAiWorkspaceProps> = ({ project }) => {
  const [activeSubTab, setActiveSubTab] = useState<'copilot' | 'predictions' | 'summarizer' | 'automation'>('copilot');

  // AI Chat State
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: 'm1',
      sender: 'ai',
      text: `Hello! I am your AI Project Assistant for ${project.projectName || 'this project'}. I have indexed project metadata, tasks, Google Drive documents, and financial metrics. How can I assist you today?`,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      ragSources: ['Project Architecture Spec', 'Google Drive OAuth 2.0 Docs', 'Sprint Backlog'],
    },
  ]);
  const [inputQuery, setInputQuery] = useState('');
  const [isThinking, setIsThinking] = useState(false);

  // Document & Meeting Summarizer State
  const [rawText, setRawText] = useState('');
  const [summaryResult, setSummaryResult] = useState<string | null>(null);
  const [generatedTasks, setGeneratedTasks] = useState<string[]>([]);
  const [isSummarizing, setIsSummarizing] = useState(false);

  // Automation Rules State
  const [rules, setRules] = useState<AutomationRule[]>([
    {
      id: 'r1',
      name: 'Auto-Assign Lead on SLA Breach',
      trigger: 'When task stays in "In Progress" > 48 hours',
      action: 'Reassign task to Technical Lead and dispatch STOMP alert',
      enabled: true,
    },
    {
      id: 'r2',
      name: 'Milestone Completion Customer Sign-off Request',
      trigger: 'When milestone status transitions to "IN_REVIEW"',
      action: 'Generate sign-off link and send email to Customer Representative',
      enabled: true,
    },
    {
      id: 'r3',
      name: 'Weekly Executive PDF Report Dispatch',
      trigger: 'Every Monday at 09:00 AM UTC',
      action: 'Compile PDF report via UniversalReportExporter and dispatch to MD/CEO',
      enabled: false,
    },
  ]);

  const [newRuleName, setNewRuleName] = useState('');
  const [newRuleTrigger, setNewRuleTrigger] = useState('When Task Status changes to "Testing"');
  const [newRuleAction, setNewRuleAction] = useState('Send notification to QA Lead');

  // Handlers
  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputQuery.trim()) return;

    const userMsg: ChatMessage = {
      id: `u-${Date.now()}`,
      sender: 'user',
      text: inputQuery.trim(),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    const query = inputQuery.trim();
    setInputQuery('');
    setIsThinking(true);

    setTimeout(() => {
      let responseText = `Based on current vector RAG indexing for ${project.projectName}, overall completion is at ${project.overallProgressPercentage || 68}%. Technical dependencies are cleared with Google Drive OAuth 2.0 active.`;
      
      if (query.toLowerCase().includes('risk') || query.toLowerCase().includes('budget')) {
        responseText = `AI Predictive Forecast: Risk likelihood is LOW (15%). Financial budget consumption is $${(project.budget || 85000).toLocaleString()} USD, tracking 4% below allocated threshold.`;
      } else if (query.toLowerCase().includes('task') || query.toLowerCase().includes('sprint')) {
        responseText = `AI Task Recommendation: 4 active tasks in current sprint. TSK-302 (Enterprise Workspace) is on critical path. Recommended next action: Trigger Customer Milestone Approval sign-off.`;
      }

      const aiMsg: ChatMessage = {
        id: `ai-${Date.now()}`,
        sender: 'ai',
        text: responseText,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        ragSources: ['MongoDB Task Graph', 'Google Drive OAuth Token Store', 'SLA Policy Engine'],
      };

      setMessages((prev) => [...prev, aiMsg]);
      setIsThinking(false);
    }, 1000);
  };

  const handleGenerateSummary = () => {
    if (!rawText.trim()) return;
    setIsSummarizing(true);
    setTimeout(() => {
      setSummaryResult(
        `AI Executive Summary:\n- Scope alignment confirmed for ${project.projectName || 'Enterprise Deliverable'}.\n- Team identified zero high-severity security blockers.\n- Target milestone sign-off date set for end of current sprint.`
      );
      setGeneratedTasks([
        'TSK-401: Complete UAT verification with customer representative',
        'TSK-402: Deploy release build to Staging environment',
        'TSK-403: Verify OAuth token refresh rotation in production logs',
      ]);
      setIsSummarizing(false);
    }, 1200);
  };

  const handleAddRule = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newRuleName.trim()) return;

    setRules([
      ...rules,
      {
        id: `r-${Date.now()}`,
        name: newRuleName.trim(),
        trigger: newRuleTrigger,
        action: newRuleAction,
        enabled: true,
      },
    ]);
    setNewRuleName('');
  };

  const toggleRule = (id: string) => {
    setRules(rules.map((r) => (r.id === id ? { ...r, enabled: !r.enabled } : r)));
  };

  return (
    <div className="space-y-6 text-slate-800 dark:text-slate-200">
      
      {/* AI Header Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-slate-950 via-indigo-950 to-slate-950 border border-indigo-900/40 text-white shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-1 bg-indigo-500/20 border border-indigo-500/30 text-indigo-300 font-mono text-xs font-bold rounded-full flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-cyan-400" /> Google GenAI Copilot Active
            </span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">AI Project Intelligence & Automation Engine</h2>
          <p className="text-xs text-slate-400 font-medium">Vector RAG Search • Predictive Risk Models • No-Code Business Rules</p>
        </div>

        {/* Sub-Tab Navigation Bar */}
        <div className="flex items-center gap-1.5 bg-slate-900/90 p-1.5 rounded-2xl border border-slate-800 shrink-0">
          {[
            { id: 'copilot', label: 'AI Copilot & RAG', icon: Bot },
            { id: 'predictions', label: 'AI Predictive Models', icon: TrendingUp },
            { id: 'summarizer', label: 'Doc & Meeting AI', icon: FileText },
            { id: 'automation', label: 'Workflow Rules', icon: Zap },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveSubTab(tab.id as any)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
                activeSubTab === tab.id
                  ? 'bg-cyan-500 text-slate-950 font-black shadow-md'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800'
              }`}
            >
              <tab.icon className="w-3.5 h-3.5" />
              <span>{tab.label}</span>
            </button>
          ))}
        </div>
      </div>

      {/* SUB-TAB 1: AI COPILOT & RAG CHAT */}
      {activeSubTab === 'copilot' && (
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <h3 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <Bot className="w-4 h-4 text-cyan-500" /> Conversational RAG Project Assistant
            </h3>
            <span className="text-xs text-slate-400 font-mono font-bold">RAG Index: 142 Documents Synced</span>
          </div>

          {/* Chat Stream */}
          <div className="h-80 overflow-y-auto space-y-3 p-4 bg-slate-50 dark:bg-slate-950/60 rounded-2xl border border-slate-200/80 dark:border-slate-800">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col space-y-1 ${msg.sender === 'user' ? 'items-end' : 'items-start'}`}
              >
                <div className="flex items-center gap-1.5 text-[10px] font-bold text-slate-400">
                  <span>{msg.sender === 'user' ? 'You' : 'AI Assistant'}</span>
                  <span>•</span>
                  <span>{msg.timestamp}</span>
                </div>

                <div
                  className={`p-3.5 rounded-2xl text-xs leading-relaxed max-w-xl ${
                    msg.sender === 'user'
                      ? 'bg-indigo-600 text-white font-medium shadow-xs'
                      : 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-800 dark:text-slate-200 font-medium shadow-xs'
                  }`}
                >
                  {msg.text}

                  {msg.ragSources && msg.ragSources.length > 0 && (
                    <div className="mt-2.5 pt-2 border-t border-slate-100 dark:border-slate-800 flex flex-wrap gap-1 text-[10px] font-mono text-cyan-600 dark:text-cyan-400">
                      <span className="font-bold text-slate-400 font-sans">RAG Sources:</span>
                      {msg.ragSources.map((src, i) => (
                        <span key={i} className="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 rounded-md">
                          {src}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}

            {isThinking && (
              <div className="flex items-center gap-2 text-xs text-slate-400 font-medium">
                <RefreshCw className="w-3.5 h-3.5 animate-spin text-cyan-500" />
                <span>Searching RAG vector index & generating response...</span>
              </div>
            )}
          </div>

          {/* Prompt Input Form */}
          <form onSubmit={handleSendMessage} className="flex gap-2">
            <input
              type="text"
              value={inputQuery}
              onChange={(e) => setInputQuery(e.target.value)}
              placeholder="Ask anything about budget, tasks, SRS docs, or SLA timeline..."
              className="flex-1 text-xs p-3 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 font-medium text-slate-900 dark:text-slate-100"
            />
            <button
              type="submit"
              disabled={isThinking || !inputQuery.trim()}
              className="px-5 py-3 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-black text-xs rounded-2xl shadow-md transition-all flex items-center gap-1.5 disabled:opacity-50"
            >
              <Send className="w-3.5 h-3.5" /> Send
            </button>
          </form>
        </div>
      )}

      {/* SUB-TAB 2: AI PREDICTIVE MODELS */}
      {activeSubTab === 'predictions' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            
            {/* Risk Forecast Card */}
            <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1">
                  <AlertTriangle className="w-3.5 h-3.5 text-amber-500" /> AI Risk Forecast
                </span>
                <span className="px-2 py-0.5 bg-emerald-500/10 text-emerald-600 font-bold text-[10px] rounded-md">LOW RISK</span>
              </div>

              <div className="space-y-1">
                <div className="text-2xl font-extrabold text-slate-900 dark:text-white font-mono">14% Risk Score</div>
                <p className="text-xs text-slate-500 font-medium">98% probability of meeting UAT acceptance criteria without security blockers.</p>
              </div>

              <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-emerald-500 rounded-full" style={{ width: '14%' }} />
              </div>
            </div>

            {/* Timeline Completion Forecast Card */}
            <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1">
                  <Clock className="w-3.5 h-3.5 text-cyan-500" /> Timeline Overrun Forecast
                </span>
                <span className="px-2 py-0.5 bg-cyan-500/10 text-cyan-600 font-bold text-[10px] rounded-md">ON TIME</span>
              </div>

              <div className="space-y-1">
                <div className="text-2xl font-extrabold text-cyan-600 dark:text-cyan-400 font-mono">0 Days Variance</div>
                <p className="text-xs text-slate-500 font-medium">Sprint velocity matches milestone delivery pace with 96% confidence.</p>
              </div>

              <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-cyan-500 rounded-full" style={{ width: '92%' }} />
              </div>
            </div>

            {/* Budget Overrun Forecast Card */}
            <div className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1">
                  <DollarSign className="w-3.5 h-3.5 text-emerald-500" /> Budget Variance Prediction
                </span>
                <span className="px-2 py-0.5 bg-emerald-500/10 text-emerald-600 font-bold text-[10px] rounded-md">UNDER BUDGET</span>
              </div>

              <div className="space-y-1">
                <div className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 font-mono">-4.2% Variance</div>
                <p className="text-xs text-slate-500 font-medium">Estimated project completion cost is $81,430 USD vs. $85,000 budget cap.</p>
              </div>

              <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div className="h-full bg-emerald-500 rounded-full" style={{ width: '95%' }} />
              </div>
            </div>

          </div>

          {/* Smart AI Recommendations */}
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
            <h3 className="text-sm font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <Lightbulb className="w-4 h-4 text-amber-500" /> Smart AI Executive Recommendations
            </h3>

            <div className="space-y-3">
              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 flex items-start gap-3">
                <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0 mt-0.5" />
                <div className="space-y-0.5">
                  <h4 className="text-xs font-extrabold text-slate-900 dark:text-white">Trigger Customer Sign-Off Gate</h4>
                  <p className="text-xs text-slate-500">Google Drive storage authentication is verified. Recommend triggering UAT sign-off request to Client VP.</p>
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 flex items-start gap-3">
                <Zap className="w-4 h-4 text-cyan-500 shrink-0 mt-0.5" />
                <div className="space-y-0.5">
                  <h4 className="text-xs font-extrabold text-slate-900 dark:text-white">Optimize Task Allocation</h4>
                  <p className="text-xs text-slate-500">Assign 2 intern cohort members to TSK-304 SLA Monitoring to accelerate sprint completion velocity.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* SUB-TAB 3: DOCUMENT & MEETING SUMMARIZER */}
      {activeSubTab === 'summarizer' && (
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-6">
          <div className="space-y-1">
            <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
              <FileText className="w-4 h-4 text-indigo-500" /> AI Document & Meeting Transcript Summarizer
            </h3>
            <p className="text-xs text-slate-500">Paste meeting transcript or project specification to extract key summaries & auto-generate tasks</p>
          </div>

          <textarea
            rows={4}
            value={rawText}
            onChange={(e) => setRawText(e.target.value)}
            placeholder="Paste meeting notes, customer email, or SRS document text here..."
            className="w-full text-xs p-3 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 font-mono text-slate-900 dark:text-slate-100"
          />

          <button
            onClick={handleGenerateSummary}
            disabled={isSummarizing || !rawText.trim()}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-2 disabled:opacity-50"
          >
            {isSummarizing ? <RefreshCw className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
            <span>Summarize & Auto-Generate Tasks</span>
          </button>

          {summaryResult && (
            <div className="p-4 rounded-2xl bg-indigo-500/5 border border-indigo-500/20 space-y-4">
              <div className="whitespace-pre-line text-xs font-medium text-slate-800 dark:text-slate-200 leading-relaxed">
                {summaryResult}
              </div>

              {generatedTasks.length > 0 && (
                <div className="space-y-2 pt-2 border-t border-indigo-500/10">
                  <span className="text-[10px] uppercase font-bold text-indigo-600 dark:text-indigo-400 block">
                    AI Auto-Generated Tasks
                  </span>
                  <div className="space-y-1.5">
                    {generatedTasks.map((tsk, idx) => (
                      <div key={idx} className="p-2.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-xs font-bold text-slate-900 dark:text-white flex items-center justify-between">
                        <span>{tsk}</span>
                        <span className="px-2 py-0.5 bg-cyan-500/10 text-cyan-600 font-mono text-[10px] rounded-md">Draft Task</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* SUB-TAB 4: WORKFLOW AUTOMATION RULES */}
      {activeSubTab === 'automation' && (
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-6">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
            <div>
              <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <Zap className="w-4 h-4 text-cyan-500" /> No-Code Automation & Event Rules Engine
              </h3>
              <p className="text-xs text-slate-500">Configure automated SLA escalations, notifications, and scheduled reports</p>
            </div>
          </div>

          {/* Active Rules List */}
          <div className="space-y-3">
            {rules.map((rule) => (
              <div
                key={rule.id}
                className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 flex items-center justify-between gap-4"
              >
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-extrabold text-slate-900 dark:text-white">{rule.name}</span>
                    <span
                      className={`px-2 py-0.5 rounded-md font-mono text-[10px] font-bold ${
                        rule.enabled
                          ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400'
                          : 'bg-slate-200 dark:bg-slate-700 text-slate-500'
                      }`}
                    >
                      {rule.enabled ? 'ACTIVE' : 'DISABLED'}
                    </span>
                  </div>

                  <p className="text-[11px] text-slate-500 font-medium">
                    <strong className="text-slate-700 dark:text-slate-300">WHEN:</strong> {rule.trigger} → <strong className="text-indigo-600 dark:text-indigo-400">THEN:</strong> {rule.action}
                  </p>
                </div>

                <button
                  onClick={() => toggleRule(rule.id)}
                  className={`px-3 py-1.5 rounded-xl font-bold text-xs transition-colors ${
                    rule.enabled
                      ? 'bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300'
                      : 'bg-emerald-600 text-white shadow-xs'
                  }`}
                >
                  {rule.enabled ? 'Disable' : 'Enable Rule'}
                </button>
              </div>
            ))}
          </div>

          {/* Create New Rule Form */}
          <form onSubmit={handleAddRule} className="p-5 rounded-2xl bg-slate-50/80 dark:bg-slate-950/60 border border-slate-200/80 dark:border-slate-800 space-y-3 pt-4">
            <h4 className="text-xs font-extrabold text-slate-900 dark:text-white uppercase tracking-wider">Add New Automation Rule</h4>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
              <div>
                <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">Rule Name *</label>
                <input
                  type="text"
                  required
                  value={newRuleName}
                  onChange={(e) => setNewRuleName(e.target.value)}
                  placeholder="e.g. Notify Manager on High Risk"
                  className="w-full text-xs p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 font-medium"
                />
              </div>

              <div>
                <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">Trigger Event *</label>
                <select
                  value={newRuleTrigger}
                  onChange={(e) => setNewRuleTrigger(e.target.value)}
                  className="w-full text-xs font-bold p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900"
                >
                  <option value='When Task Status changes to "Testing"'>When Task Status changes to "Testing"</option>
                  <option value='When Risk Severity equals "HIGH"'>When Risk Severity equals "HIGH"</option>
                  <option value='When Budget exceeds 90% threshold'>When Budget exceeds 90% threshold</option>
                  <option value='When Milestone sign-off approved'>When Milestone sign-off approved</option>
                </select>
              </div>

              <div>
                <label className="font-bold text-slate-700 dark:text-slate-300 block mb-1">Automated Action *</label>
                <select
                  value={newRuleAction}
                  onChange={(e) => setNewRuleAction(e.target.value)}
                  className="w-full text-xs font-bold p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900"
                >
                  <option value="Send notification to QA Lead">Send notification to QA Lead</option>
                  <option value="Dispatch STOMP WebSocket alert to Project Manager">Dispatch STOMP WebSocket alert to Project Manager</option>
                  <option value="Generate PDF Summary report via UniversalReportExporter">Generate PDF Summary report via UniversalReportExporter</option>
                  <option value="Auto-assign task to Technical Lead">Auto-assign task to Technical Lead</option>
                </select>
              </div>
            </div>

            <button
              type="submit"
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs rounded-xl shadow-md transition-all flex items-center gap-1.5"
            >
              <Plus className="w-3.5 h-3.5" /> Save Rule
            </button>
          </form>
        </div>
      )}

    </div>
  );
};
