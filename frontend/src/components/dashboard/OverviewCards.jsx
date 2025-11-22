import React from 'react'

export default function OverviewCards({ 
  totalUsers = 0, 
  totalIssues = 0, 
  aiAnalysisCount = 0,
  criticalIssues = 0,
  warningIssues = 0,
  suggestionIssues = 0,
  cleanFiles = 0
}) {
  // Calculate quality score and grade
  const calculateQualityScore = () => {
    if (totalIssues === 0) return 100;
    const issueWeight = (criticalIssues * 10) + (warningIssues * 6) + (suggestionIssues * 3);
    const baseScore = Math.max(0, 100 - (totalIssues * 5));
    const severityPenalty = Math.min(50, issueWeight * 2);
    return Math.max(0, Math.round(baseScore - severityPenalty));
  };

  const getQualityGrade = (score) => {
    if (score >= 90) return { grade: 'A+', color: 'text-green-600', bg: 'bg-green-100' };
    if (score >= 80) return { grade: 'A', color: 'text-green-600', bg: 'bg-green-100' };
    if (score >= 70) return { grade: 'B', color: 'text-blue-600', bg: 'bg-blue-100' };
    if (score >= 60) return { grade: 'C', color: 'text-yellow-600', bg: 'bg-yellow-100' };
    if (score >= 50) return { grade: 'D', color: 'text-orange-600', bg: 'bg-orange-100' };
    return { grade: 'F', color: 'text-red-600', bg: 'bg-red-100' };
  };

  const qualityScore = calculateQualityScore();
  const qualityGrade = getQualityGrade(qualityScore);

  return (
    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div className={`p-4 rounded-lg shadow flex flex-col text-center ${qualityGrade.bg}`}>
        <div className="text-sm text-gray-500">Quality Score</div>
        <div className={`mt-2 text-3xl font-bold ${qualityGrade.color}`}>{qualityScore}</div>
        <div className={`text-lg font-semibold ${qualityGrade.color}`}>Grade: {qualityGrade.grade}</div>
        <div className="text-xs text-gray-500 mt-1">
          {totalIssues === 0 ? 'Excellent Code!' : 'Code Quality'}
        </div>
      </div>

      <div className="p-4 bg-white rounded-lg shadow flex flex-col">
        <div className="text-sm text-gray-500">Total Issues</div>
        <div className="mt-2 text-2xl font-semibold">{totalIssues ?? 0}</div>
        <div className="text-xs text-gray-500 mt-1">
          {criticalIssues > 0 && <span className="text-red-500">🔴 {criticalIssues} Critical</span>}
          {warningIssues > 0 && <span className="text-yellow-500 ml-2">🟡 {warningIssues} High</span>}
        </div>
      </div>

      <div className="p-4 bg-white rounded-lg shadow flex flex-col">
        <div className="text-sm text-gray-500">Clean Files</div>
        <div className="mt-2 text-2xl font-semibold">{cleanFiles ?? 0}</div>
        <div className="text-xs text-gray-500 mt-1">
          Files without issues
        </div>
      </div>

      <div className="p-4 bg-white rounded-lg shadow flex flex-col">
        <div className="text-sm text-gray-500">Issue Severity Distribution</div>
        <div className="mt-2 space-y-1">
          <div className="flex justify-between text-sm">
            <span>critical</span>
            <span className="font-semibold">{criticalIssues ?? 0}</span>
          </div>
          <div className="flex justify-between text-sm">
            <span>high</span>
            <span className="font-semibold">{warningIssues ?? 0}</span>
          </div>
          <div className="flex justify-between text-sm">
            <span>medium</span>
            <span className="font-semibold">{suggestionIssues ?? 0}</span>
          </div>
          <div className="flex justify-between text-sm">
            <span>low</span>
            <span className="font-semibold">0</span>
          </div>
        </div>
      </div>
    </div>
  )
}

